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
import ru.geekbrains.WowVendorTeamHelper.model.Role;
import ru.geekbrains.WowVendorTeamHelper.model.User;
import ru.geekbrains.WowVendorTeamHelper.repository.RoleRepository;
import ru.geekbrains.WowVendorTeamHelper.repository.UserRepository;
import ru.geekbrains.WowVendorTeamHelper.utils.JwtTokenUtil;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PrivilegeService privilegeService;

    private final StatusService statusService;
    private final PasswordEncoder bCryptPasswordEncoder;
    private final JwtTokenUtil jwtTokenUtil;
    private final AuthenticationManager authenticationManager;
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

    public UserDto createUser(UserDto userDto) {

        if (userRepository.findByUsername(userDto.getUsername()).isPresent()) {
            throw new ResourceNotFoundException("Извините, но такой пользователь уже зарегестрирован");
        }
        User user = new User();
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setRoles(Arrays.asList(roleRepository.findByTitle(ROLEUSER).orElseThrow()));
        user.setStatus(statusService.findByTitle(NOTAPPROVED));
        user.setActivationCode(user.getUsername() + "_" + UUID.randomUUID());
        return new UserDto(userRepository.save(user));
    }

    public boolean userApproved(Long userId, Long statusId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new ResourceNotFoundException("Не удается найти пользователя с идентификатором: " + userId));
        user.setStatus(statusService.findById(statusId));
        userRepository.save(user);
        return true;
    }

    public ResponseEntity<?> authenticationUser(JwtRequest authRequest) {
        User user = userRepository.findByUsername(authRequest.getUsername()).orElseThrow(() ->
                new ResourceNotFoundException("Не найдено пользователя с логином: " + authRequest.getUsername()));
        if (user.getStatus().equals(NOTAPPROVED)) {
            return new ResponseEntity(new AppError(HttpStatus.FORBIDDEN.value(), "Регистрация пользователя не была подтверждена"), HttpStatus.FORBIDDEN);
        }
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(new AppError(HttpStatus.UNAUTHORIZED.value(), "Неправильный логин или пароль. Проверьте правильность учётных данных."), HttpStatus.UNAUTHORIZED);
        }
        UserDetails userDetails = loadUserByUsername(authRequest.getUsername());
        String token = jwtTokenUtil.generateToken(userDetails);
        log.info("Пользователь с таким именем был авторизован: " + authRequest.getUsername());
        return ResponseEntity.ok(new JwtResponse(token));
    }

    public List<UserDto> findUsersByStatus(String status) {
        return userRepository.findAllByStatus(status).stream().map(UserDto::new).collect(Collectors.toList());
    }

    public List<UserDto> findUsersByPrivilege(String privilege) {
        return userRepository.findAllByPrivilege(privilege).stream().map(UserDto::new).collect(Collectors.toList());
    }

    public List<UserDto> findUsersByRole(String role) {
        String roleFormatted = "ROLE_" + role.toUpperCase();
        System.out.println(roleFormatted);
        return userRepository.findAllByRole(roleFormatted).stream().map(UserDto::new).collect(Collectors.toList());
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream().map(UserDto::new).collect(Collectors.toList());
    }

    @Transactional
    public UserDto addPrivilegeToUser(Long userId, Long privilegeId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new ResourceNotFoundException("Не найдено пользователя с id: " + userId));
        user.getPrivileges().add(privilegeService.findById(privilegeId));
        return new UserDto(user);
    }

    @Transactional
    public boolean deletePrivilegeFromUser(Long userId, Long privilegeId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new ResourceNotFoundException("Не найдено пользователя с id: " + userId));
        user.getPrivileges().remove(privilegeService.findById(privilegeId));
        return true;
    }
}