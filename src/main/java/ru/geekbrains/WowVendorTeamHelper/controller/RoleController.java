package ru.geekbrains.WowVendorTeamHelper.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.geekbrains.WowVendorTeamHelper.dto.RoleDto;
import ru.geekbrains.WowVendorTeamHelper.mapper.RoleMapper;
import ru.geekbrains.WowVendorTeamHelper.service.RoleService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/role")
public class RoleController {
    private final RoleService roleService;

    private final RoleMapper roleMapper;

    @GetMapping()
    public List<RoleDto> getAllRole() {
        return roleService.getAllRoles().stream().map(roleMapper::toDto).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public RoleDto findById(@PathVariable Long id){
        return roleMapper.toDto(roleService.findById(id));
    }

    @PostMapping()
    public RoleDto addRole(@RequestBody RoleDto roleDto) {
        return roleMapper.toDto(roleService.addRole(roleDto));
    }

    @DeleteMapping("/{id}")
    public boolean deleteRole(@PathVariable Long id) {
        return roleService.deleteRoleById(id);
    }
}
