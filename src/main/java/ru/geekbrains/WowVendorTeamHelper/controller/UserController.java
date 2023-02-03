package ru.geekbrains.WowVendorTeamHelper.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.geekbrains.WowVendorTeamHelper.model.User;
import ru.geekbrains.WowVendorTeamHelper.service.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/status")
    public List<User> findUsersByStatus(@RequestParam String status) {
        return userService.findUsersByStatus(status);
    }

    @GetMapping("/role")
    public List<User> findUsersByRole(@RequestParam String role) {
        return userService.findUsersByRole(role);
    }

    @GetMapping()
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }
}
