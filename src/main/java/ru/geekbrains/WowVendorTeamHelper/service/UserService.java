package ru.geekbrains.WowVendorTeamHelper.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import ru.geekbrains.WowVendorTeamHelper.dto.JwtRequest;
import ru.geekbrains.WowVendorTeamHelper.dto.JwtResponse;
import ru.geekbrains.WowVendorTeamHelper.dto.RegistrationRequest;
import ru.geekbrains.WowVendorTeamHelper.dto.UserDto;
import ru.geekbrains.WowVendorTeamHelper.exeptions.AppError;
import ru.geekbrains.WowVendorTeamHelper.exeptions.ResourceNotFoundException;
import ru.geekbrains.WowVendorTeamHelper.mapper.UserMapper;
import ru.geekbrains.WowVendorTeamHelper.model.Role;
import ru.geekbrains.WowVendorTeamHelper.model.Status;
import ru.geekbrains.WowVendorTeamHelper.model.User;
import ru.geekbrains.WowVendorTeamHelper.repository.UserRepository;
import ru.geekbrains.WowVendorTeamHelper.utils.JwtTokenUtil;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private static final String NOT_APPROVED = "not_approved";
    private static final String ROLE_USER = "ROLE_USER";
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleService roleService;
    private final PrivilegeService privilegeService;
    private final StatusService statusService;
    private final PasswordEncoder bCryptPasswordEncoder;
    private final JwtTokenUtil jwtTokenUtil;
    private final AuthenticationManager authenticationManager;
    private final MailService mailService;


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

    public ResponseEntity<?> createUser(RegistrationRequest request) {
        if (findByEmail(request.getEmail()) != null) {
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "Пользователь с таким " +
                    "Е-mail " + request.getEmail() + " уже зарегистрирован."), HttpStatus.BAD_REQUEST);
        } else if (findByUsername(request.getUsername()) != null) {
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "Пользователь с таким " +
                    "Username " + request.getUsername() + " уже зарегистрирован."), HttpStatus.BAD_REQUEST);
        } else {
            User newUser = new User();
            newUser.setEmail(request.getEmail());
            newUser.setUsername(request.getUsername());
            newUser.setPassword(bCryptPasswordEncoder.encode(request.getPassword()));
            newUser.setActivationCode(request.getUsername() + "_" + UUID.randomUUID());
            newUser.setStatus(statusService.findByTitle(NOT_APPROVED));
            newUser.setRoles(List.of(roleService.getRoleByTitle(ROLE_USER)));
            newUser.setPrivileges(List.of());
            mailService.sendHtmlMessage(userDto.getEmail(), "Заявка на регистрацию принята!", "mail-registration.html", new HashMap<>());
            return new ResponseEntity<>(userMapper.toDto(userRepository.save(newUser)), HttpStatus.CREATED);
        }
    }

    public boolean userApproved(Long userId, Long statusId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new ResourceNotFoundException("Не удается найти пользователя с идентификатором: " + userId));
        Status status = statusService.findById(statusId);
        String subject = "";
        String template = "";
        if(status.getTitle().equals(APPROVED)) {
            subject = "Регистрация одобрена!";
            template = "mail-approved.html";
            user.setStatus(status);
            userRepository.save(user);

        } else if (status.getTitle().equals(NOTAPPROVED)){
            subject = "Заявка на регистрацию отклонена!";
            template = "mail-not-approved.html";
            userRepository.delete(user);
        }
        mailService.sendHtmlMessage(user.getEmail(), subject, template, new HashMap<>());
        return true;
    }

    public ResponseEntity<?> authenticationUser(JwtRequest authRequest) {
        User user;
        if (findByEmail(authRequest.getLogin()) != null) {
            user = findByEmail(authRequest.getLogin());
        } else if (findByUsername(authRequest.getLogin()) != null) {
            user = findByUsername(authRequest.getLogin());
        } else {
            throw new ResourceNotFoundException("Пользователя с таким логином " + authRequest.getLogin() + " не существует");
        }
        if (user.getStatus().getTitle().equals(NOT_APPROVED)) {
            return new ResponseEntity<>(new AppError(HttpStatus.FORBIDDEN.value(), "Авторизация прошла успешно, но аккаунт " + authRequest.getLogin() + " не был одобрен со стороны администрации."), HttpStatus.FORBIDDEN);
        }
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getLogin(), authRequest.getPassword()));
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(new AppError(HttpStatus.UNAUTHORIZED.value(), "Неправильный логин или пароль. Проверьте правильность учётных данных."), HttpStatus.UNAUTHORIZED);
        }
        UserDetails userDetails = loadUserByUsername(user.getUsername());
        String token = jwtTokenUtil.generateToken(userDetails);
        log.info("Пользователь с таким логином был авторизован: " + authRequest.getLogin());
        return ResponseEntity.ok(new JwtResponse(token));
    }

    public User findByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.orElse(null);
    }

    public List<UserDto> findUsersByStatus(String status) {
        return userRepository.findAllByStatus(status).stream().map(UserDto::new).collect(Collectors.toList());
    }

    public List<UserDto> findUsersByPrivilege(String privilege) {
        return userRepository.findAllByPrivilege(privilege).stream().map(userMapper::toDto).collect(Collectors.toList());
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
                new ResourceNotFoundException("Не найдено пользователя с id: " + userId));
        user.getPrivileges().add(privilegeService.findById(privilegeId));
        return userMapper.toDto(user);
    }

    @Transactional
    public boolean deletePrivilegeFromUser(Long userId, Long privilegeId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new ResourceNotFoundException("Не найдено пользователя с id: " + userId));
        user.getPrivileges().remove(privilegeService.findById(privilegeId));
        return true;
    }

    @Transactional
    public UserDto addRoleToUser(Long userId, Long roleId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new ResourceNotFoundException("Не найдено пользователя с id: " + userId));
        user.getRoles().add(roleService.findById(roleId));
       return userMapper.toDto(user);
    }

    @Transactional
    public boolean deleteRoleFromUser(Long userId, Long roleId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new ResourceNotFoundException("Не найдено пользователя с id: " + userId));
        user.getRoles().remove(roleService.deleteRoleById(roleId));
        return true;
    }
}