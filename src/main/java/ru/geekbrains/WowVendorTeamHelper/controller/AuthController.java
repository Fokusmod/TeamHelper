package ru.geekbrains.WowVendorTeamHelper.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.geekbrains.WowVendorTeamHelper.dto.JwtRequest;
import ru.geekbrains.WowVendorTeamHelper.dto.UserDto;
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
            @RequestBody UserDto userDto) {
        return userService.createUser(userDto);
    }
}