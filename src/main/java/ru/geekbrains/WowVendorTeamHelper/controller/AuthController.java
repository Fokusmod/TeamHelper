package ru.geekbrains.WowVendorTeamHelper.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.geekbrains.WowVendorTeamHelper.dto.JwtRequest;
import ru.geekbrains.WowVendorTeamHelper.dto.RegistrationRequest;
import ru.geekbrains.WowVendorTeamHelper.service.UserService;


@Controller
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;


    @ResponseBody
    @PostMapping("/auth")
    public ResponseEntity<?> createAuthToken(@RequestBody JwtRequest authRequest) {
        return userService.authenticationUser(authRequest);
    }

    @ResponseBody
    @PostMapping("/registration")
    public ResponseEntity<?> registration(@RequestBody RegistrationRequest request) {
        return userService.saveUserToCacheFromDto(request);
    }

    @GetMapping("/activate/{code}")
    public String activate(@PathVariable String code) {
        if (userService.createUser(code)) {
            return "page-after-registration";
        }
        return "page-link-relevant";
    }
}