package ru.geekbrains.WowVendorTeamHelper.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.geekbrains.WowVendorTeamHelper.dto.UserDto;
import ru.geekbrains.WowVendorTeamHelper.dto.UserRoleRequest;
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

    @PostMapping("/{userId}/privilege/{privilegeId}")
    public UserDto addPrivilegeToUser(@PathVariable Long userId, @PathVariable Long privilegeId) {
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

    @PutMapping("/role")
    public UserDto userAddRole(@RequestBody UserRoleRequest userRoleRequest) {
        return userService.addRoleToUser(userRoleRequest);
    }

    @DeleteMapping("/role")
    public boolean userDeleteRole(@RequestBody UserRoleRequest userRoleRequest) {
        return userService.deleteRoleFromUser(userRoleRequest);
    }
}
