package ru.geekbrains.WowVendorTeamHelper.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.geekbrains.WowVendorTeamHelper.dto.*;
import ru.geekbrains.WowVendorTeamHelper.exeptions.*;
import ru.geekbrains.WowVendorTeamHelper.mapper.UserMapper;
import ru.geekbrains.WowVendorTeamHelper.model.Role;
import ru.geekbrains.WowVendorTeamHelper.model.Status;
import ru.geekbrains.WowVendorTeamHelper.model.User;
import ru.geekbrains.WowVendorTeamHelper.repository.UserRepository;
import ru.geekbrains.WowVendorTeamHelper.utils.JwtTokenUtil;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    @Value("${my-config.url-activate}")
    private String activationUrl;
    private static final String NOT_APPROVED = "not_approved";
    private static final String APPROVED = "approved";
    private static final String ROLE_USER = "ROLE_USER";
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PrivilegeService privilegeService;
    private final StatusService statusService;
    private final PasswordEncoder bCryptPasswordEncoder;
    private final JwtTokenUtil jwtTokenUtil;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;
    private final MailService mailService;
    private final RedisTemplate<String, Object> redisTemplate;


    public User findByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        return user.orElse(null);
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) {
        User user = findByUsername(username);
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), mapRolesToAuthorities(user.getRoles()));
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getTitle())).collect(Collectors.toList());
    }

    public void saveUserToCacheFromDto(RegistrationRequest request) {
        if (findByEmail(request.getEmail()) != null) {
            throw new WWTHBadRequestException("Пользователь с таким Е-mail " + request.getEmail() + " уже зарегистрирован.");
        } else if (findByUsername(request.getUsername()) != null) {
            throw new WWTHBadRequestException("Пользователь с таким Username " + request.getUsername() + " уже зарегистрирован.");
        }
        String activationCode = request.getEmail() + "_" + UUID.randomUUID();
        try {
            redisTemplate.opsForValue().set(activationCode, request);
        } catch (RedisConnectionFailureException redisException) {
            log.error("Connecting error to Redis");
        }
        if (Boolean.FALSE.equals(redisTemplate.hasKey(activationCode))) {
            log.debug("Регистрационные данные не были сохранены в Redis");
        }
        Map<String, Object> properties = new HashMap<>();
        properties.put("activationUrl", activationUrl);
        properties.put("activationCode", activationCode);
        mailService.sendHtmlMessage(request.getEmail(), "Подтверждение регистрации!", "mail-activation.html", properties);
    }

    public boolean createUser(String activatedCode) {
        RegistrationRequest request = null;
        try {
            request = (RegistrationRequest) redisTemplate.opsForValue().get(activatedCode);
        } catch (RuntimeException e) {
            log.error("Connecting error to Redis");
        }
        if (request == null) {
            log.debug("unable to load data from Redis");
        }

        if (userRepository.findByEmail(request.getEmail()).isEmpty()) {
            User newUser = new User();
            newUser.setEmail(request.getEmail());
            newUser.setUsername(request.getUsername());
            newUser.setPassword(bCryptPasswordEncoder.encode(request.getPassword()));
            newUser.setStatus(statusService.findByTitle(NOT_APPROVED));
            newUser.setRoles(List.of(roleService.getRoleByTitle(ROLE_USER)));
            newUser.setPrivileges(List.of());
            userRepository.save(newUser);
            log.info("User " + newUser.getEmail() + " created!");
            mailService.sendHtmlMessage(request.getEmail(), "Заявка на регистрацию принята!", "mail-registration.html", new HashMap<>());
            return true;
        }
        return false;
    }


    public ResponseEntity<?> updatePassword(RequestChangePassword requestChangePassword, Principal principal) {

        if (requestChangePassword.getOldPassword() == null || requestChangePassword.getNewPassword() == null
                || requestChangePassword.getEmail() == null) {
            log.info("Пользователь не ввел один из паролей, или email");
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "Необходимо ввести старый и новый пароли, а также email"), HttpStatus.BAD_REQUEST);
        }

        if (principal == null) {
            log.info("В запросе нет токена");
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "Запрос должен быть с авторизацией"), HttpStatus.BAD_REQUEST);
        }

        User user = findByEmail(requestChangePassword.getEmail());

        if (user == null) {
            throw new WWTHResourceNotFoundException("Пользователь с email: " + requestChangePassword.getEmail() + ", не найден.");
        }

        if (!user.getUsername().equals(principal.getName())) {
            log.info("Пользователь с именем: " + principal.getName() + ", хочет изменить пароль пользователя с именем: " + user.getUsername());
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "Вы не можете сменить пароль. Введенный вами email, не принадлежит вам."), HttpStatus.BAD_REQUEST);
        }


        if (!bCryptPasswordEncoder.matches(requestChangePassword.getOldPassword(), user.getPassword())) {
            log.info("При смене пароля пользователь ввел неверный старый пароль");
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "Введенный вами старый пароль не верен, " +
                    "если вы забыли пароль, обратитесь к администратору"), HttpStatus.BAD_REQUEST);
        }

        user.setPassword(bCryptPasswordEncoder.encode(requestChangePassword.getNewPassword()));
        mailService.sendHtmlMessage(user.getEmail(), "Пароль изменен!", "mail-change-password.html", new HashMap<>());
        return new ResponseEntity<>(userMapper.toDto(userRepository.save(user)), HttpStatus.OK);
    }

    public boolean userApproved(Long userId, Long statusId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new WWTHResourceNotFoundException("Не удается найти пользователя с идентификатором: " + userId));
        Status status = statusService.findById(statusId);
        String subject = "";
        String template = "";
        if (status.getTitle().equals(APPROVED)) {
            subject = "Регистрация одобрена!";
            template = "mail-approved.html";
            user.setStatus(status);
            userRepository.save(user);

        } else if (status.getTitle().equals(NOT_APPROVED)) {
            subject = "Заявка на регистрацию отклонена!";
            template = "mail-not-approved.html";
            userRepository.delete(user);
        }
        mailService.sendHtmlMessage(user.getEmail(), subject, template, new HashMap<>());
        return true;
    }

    public JwtResponse authenticationUser(JwtRequest authRequest) {
        User user;
        if (findByEmail(authRequest.getLogin()) != null) {
            user = findByEmail(authRequest.getLogin());
        } else if (findByUsername(authRequest.getLogin()) != null) {
            user = findByUsername(authRequest.getLogin());
        } else {
            throw new WWTHResourceNotFoundException("Пользователя с таким логином " + authRequest.getLogin() + " не существует");
        }
        if (user.getStatus().getTitle().equals(NOT_APPROVED)) {
            throw new WWTHForbiddenException("Авторизация прошла успешно, но аккаунт " + authRequest.getLogin() + " не был одобрен со стороны администрации.");
        }
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getLogin(), authRequest.getPassword()));
        } catch (BadCredentialsException e) {
            throw new WWTHBadRequestException("Неправильный логин или пароль. Проверьте правильность учётных данных.");
        }
        UserDetails userDetails = loadUserByUsername(user.getUsername());
        String token = jwtTokenUtil.generateToken(userDetails, user);
        log.info("Пользователь с логином " + authRequest.getLogin() + " был авторизован: ");
        return new JwtResponse(token);
    }

    public List<UserDto> findUsersByStatus(String status) {
        return userRepository.findAllByStatus(statusService.findByTitle(status)).stream().map(userMapper::toDto).collect(Collectors.toList());
    }

    public List<UserDto> findUsersByPrivilege(String privilege) {
        return userRepository.findAllByPrivilege(privilege).stream().map(userMapper::toDto).collect(Collectors.toList());
    }

    public User findByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.orElse(null);
    }

    public ResponseEntity<?> checkUserByEmail(RequestEmail requestEmail) {
        if (requestEmail.getEmail() == null) {
            log.info("В запросе не указан email");
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "Не указан email"), HttpStatus.BAD_REQUEST);
        }

        User user = findByEmail(requestEmail.getEmail());
        if (user == null) {
            throw new WWTHResourceNotFoundException("Пользователь с email: " + requestEmail.getEmail() + ", не найден.");
        }
        return new ResponseEntity<>(userMapper.toDto(user), HttpStatus.OK);
    }

    public List<UserDto> findUsersByRole(String role) {
        String roleFormatted = "ROLE_" + role.toUpperCase();
        System.out.println(roleFormatted);
        return userRepository.findAllByRole(roleFormatted).stream().map(userMapper::toDto).collect(Collectors.toList());
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream().map(userMapper::toDto).collect(Collectors.toList());
    }

    @Transactional
    public UserDto addPrivilegeToUser(Long userId, Long privilegeId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new WWTHResourceNotFoundException("Не найдено пользователя с id: " + userId));
        user.getPrivileges().add(privilegeService.findById(privilegeId));
        return userMapper.toDto(user);
    }

    @Transactional
    public boolean deletePrivilegeFromUser(Long userId, Long privilegeId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new WWTHResourceNotFoundException("Не найдено пользователя с id: " + userId));
        user.getPrivileges().remove(privilegeService.findById(privilegeId));
        return true;
    }

    @Transactional
    public UserDto addRoleToUser(UserRoleRequest userRoleRequest) {
        User user = userRepository.findById(userRoleRequest.getUserId()).orElseThrow(() ->
                new WWTHResourceNotFoundException("Не найдено пользователя с id: " + userRoleRequest.getUserId()));
        user.getRoles().add(roleService.findById(userRoleRequest.getRoleId()));
        return userMapper.toDto(user);
    }

    @Transactional
    public boolean deleteRoleFromUser(UserRoleRequest userRoleRequest) {
        User user = userRepository.findById(userRoleRequest.getUserId()).orElseThrow(() ->
                new WWTHResourceNotFoundException("Не найдено пользователя с id: " + userRoleRequest.getUserId()));
        user.getRoles().remove(roleService.deleteRoleById(userRoleRequest.getRoleId()));
        return true;
    }
}