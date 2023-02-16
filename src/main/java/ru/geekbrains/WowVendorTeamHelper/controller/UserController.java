package ru.geekbrains.WowVendorTeamHelper.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.geekbrains.WowVendorTeamHelper.dto.UserDto;
import ru.geekbrains.WowVendorTeamHelper.dto.UserPrivilegeRequest;
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

    @PostMapping("/add_privilege")
    public UserDto addPrivilegeToUser(@RequestBody UserPrivilegeRequest userPrivilegeRequest) {
        return userService.addPrivilegeToUser(userPrivilegeRequest);
    }

    @DeleteMapping("/delete_privilege")
    public boolean deletePrivilegeFromUser(@RequestBody UserPrivilegeRequest userPrivilegeRequest) {
        return userService.deletePrivilegeFromUser(userPrivilegeRequest);
    }

    @PostMapping("/{userId}/approved/{statusId}")
    public boolean userApproved(@PathVariable Long userId, @PathVariable Long statusId) {
        return userService.userApproved(userId, statusId);
    }

    @PutMapping("/add_role")
    public UserDto userAddRole(@RequestBody UserRoleRequest userRoleRequest) {
        return userService.addRoleToUser(userRoleRequest);
    }

    @DeleteMapping("/delete_role")
    public boolean userDeleteRole(@RequestBody UserRoleRequest userRoleRequest) {
        return userService.deleteRoleFromUser(userRoleRequest);
    }
}
