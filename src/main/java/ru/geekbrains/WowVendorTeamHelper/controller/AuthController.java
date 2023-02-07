package ru.geekbrains.WowVendorTeamHelper.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.geekbrains.WowVendorTeamHelper.dto.JwtRequest;
import ru.geekbrains.WowVendorTeamHelper.exeptions.RegistrationException;
import ru.geekbrains.WowVendorTeamHelper.model.User;
import ru.geekbrains.WowVendorTeamHelper.service.UserService;


@RestController
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;


    @PostMapping("/auth")
    public ResponseEntity<?> createAuthToken(@RequestBody JwtRequest authRequest) {
        return userService.authenticationUser(authRequest);
    }

    @PostMapping("/registration")
    public ResponseEntity<?> registration(
            @RequestBody User user) {
        if (!userService.addUser(user)) {
            return new ResponseEntity<>(
                    new RegistrationException("Sorry, but such user already exists"), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(
                new RegistrationException("Your application for registration is under consideration," +
                " wait for confirmation"), HttpStatus.OK);
    }


    @PostMapping("/user_approved/{id}")
    public boolean userApproved(@PathVariable Long id) {
        return userService.userApproved(id);
    }
    
}