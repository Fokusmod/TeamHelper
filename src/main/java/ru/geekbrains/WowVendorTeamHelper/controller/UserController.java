package ru.geekbrains.WowVendorTeamHelper.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.geekbrains.WowVendorTeamHelper.dto.UserDto;
import ru.geekbrains.WowVendorTeamHelper.service.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/user")
public class UserController {

    private final UserService userService;


    @GetMapping()
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/status")
    public List<UserDto> findUsersByStatus(@RequestParam String status) {
        return userService.findUsersByStatus(status);
    }

    @GetMapping("/role")
    public List<UserDto> findUsersByRole(@RequestParam String role) {
        return userService.findUsersByRole(role);
    }

    @GetMapping("/privilege")
    public List<UserDto> findUsersByPrivilege(@RequestParam String privilege) {
        return userService.findUsersByPrivilege(privilege);
    }

    @PostMapping("/{userId}/add_privilege/{privilegeId}")
    public UserDto addPrivilegeToUser(@PathVariable Long userId, @PathVariable Long privilegeId) {
        return userService.addPrivilegeToUser(userId, privilegeId);
    }

    @DeleteMapping("/{userId}/delete_privilege/{privilegeId}")
    public boolean deletePrivilegeFromUser(@PathVariable Long userId, @PathVariable Long privilegeId) {
        return userService.deletePrivilegeFromUser(userId, privilegeId);
    }

    @PostMapping("/{userId}/approved/{statusId}")
    public boolean userApproved(@PathVariable Long userId, @PathVariable Long statusId) {
        return userService.userApproved(userId, statusId);
    }

    @PutMapping("/{userId}/add_role/{roleId}")
    public UserDto userAddRole(@PathVariable Long userId, @PathVariable Long roleId) {
        return userService.addRoleToUser(userId, roleId);
    }

    @DeleteMapping("/{userId}/delete_role/{roleId}")
    public boolean userDeleteRole(@PathVariable Long userId, @PathVariable Long roleId) {
        return userService.deleteRoleFromUser(userId, roleId);
    }
}
