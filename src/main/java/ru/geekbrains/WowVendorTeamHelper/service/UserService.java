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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.geekbrains.WowVendorTeamHelper.dto.JwtRequest;
import ru.geekbrains.WowVendorTeamHelper.dto.JwtResponse;
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
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleService roleService;
    private final PrivilegeService privilegeService;

    private final StatusService statusService;
    private final PasswordEncoder bCryptPasswordEncoder;
    private final JwtTokenUtil jwtTokenUtil;
    private final AuthenticationManager authenticationManager;

    private final MailService mailService;
    static final String NOTAPPROVED = "not_approved";
    static final String APPROVED = "approved";
    static final String ROLEUSER = "ROLE_USER";



    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(String.format("User '%s' not found", username)));
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = findByUsername(username);

        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), mapRolesToAuthorities(user.getRoles()));
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getTitle())).collect(Collectors.toList());
    }

    public ResponseEntity<?> createUser(UserDto userDto) {

        if (userRepository.findByUsername(userDto.getUsername()).isPresent()) {

            return new ResponseEntity(new AppError(HttpStatus.BAD_REQUEST.value(), "Извините, но такой пользователь уже зарегестрирован"), HttpStatus.BAD_REQUEST);
        }
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
        user.setRoles(Arrays.asList(roleService.findByTitle(ROLEUSER)));
        user.setStatus(statusService.findByTitle(NOTAPPROVED));
        user.setActivationCode(user.getUsername() + "_" + UUID.randomUUID());
        user.setPrivileges(new ArrayList<>());
        user.setEmail(userDto.getEmail());

        mailService.sendHtmlMessage(userDto.getEmail(), "Заявка на регистрацию принята!", "mail-registration.html", new HashMap<>());

        return new ResponseEntity<>(userMapper.toDto(userRepository.save(user)), HttpStatus.CREATED);
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
        User user = userRepository.findByUsername(authRequest.getUsername()).orElseThrow(() ->
                new ResourceNotFoundException("Не найдено пользователя с логином: " + authRequest.getUsername()));
        if (user.getStatus().getTitle().equals(NOTAPPROVED)) {
            return new ResponseEntity(new AppError(HttpStatus.FORBIDDEN.value(), "Регистрация пользователя не была подтверждена"), HttpStatus.FORBIDDEN);
        }
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(new AppError(HttpStatus.UNAUTHORIZED.value(), "Неправильный логин или пароль. Проверьте правильность учётных данных."), HttpStatus.UNAUTHORIZED);
        }
        UserDetails userDetails = loadUserByUsername(authRequest.getUsername());
        String token = jwtTokenUtil.generateToken(userDetails, user);
        log.info("Пользователь " + authRequest.getUsername() + " был авторизован" );
        return ResponseEntity.ok(new JwtResponse(token));
    }

    public List<UserDto> findUsersByStatus(String statusTitle) {
        Status status = statusService.findByTitle(statusTitle);
        return userRepository.findAllByStatus(status).stream().map(userMapper::toDto).collect(Collectors.toList());
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