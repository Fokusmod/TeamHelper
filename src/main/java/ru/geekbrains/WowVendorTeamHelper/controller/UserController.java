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


    @GetMapping()
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/status")
    public List<User> findUsersByStatus(@RequestParam String status) {
        return userService.findUsersByStatus(status);
    }

    @GetMapping("/role")
    public List<User> findUsersByRole(@RequestParam String role) {
        return userService.findUsersByRole(role);
    }

    @GetMapping("/privilege")
    public List<User> findUsersByPrivilege(@RequestParam String privilege) {
        return userService.findUsersByPrivilege(privilege);
    }

    @PostMapping("/{userId}/privilege/{privilegeId}")
    public User addPrivilegeToUser(@PathVariable Long userId, @PathVariable Long privilegeId) {
        return userService.addPrivilegeToUser(userId, privilegeId);
    }

    @DeleteMapping("/{userId}/privilege/{privilegeId}")
    public boolean deletePrivilegeFromUser(@PathVariable Long userId, @PathVariable Long privilegeId) {
        return userService.deletePrivilegeFromUser(userId, privilegeId);
    }

    @PostMapping("/{userId}/status/{statusId}")
    public boolean userApproved(@PathVariable Long userId, @PathVariable Long statusId) {
        return userService.userApproved(userId, statusId);
    }
}
