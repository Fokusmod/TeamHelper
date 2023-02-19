package ru.geekbrains.WowVendorTeamHelper.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.geekbrains.WowVendorTeamHelper.dto.RoleDto;
import ru.geekbrains.WowVendorTeamHelper.exeptions.ResourceNotFoundException;
import ru.geekbrains.WowVendorTeamHelper.model.Role;
import ru.geekbrains.WowVendorTeamHelper.repository.RoleRepository;

import java.util.List;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;


    public Role findById(Long id) {
        return roleRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Не удается найти роль с идентификатором: " + id));
    }

    public Role addRole(RoleDto roleDto) {
        Role role = new Role();
        String roleFormatted = "ROLE_" + roleDto.getTitle().toUpperCase();
        role.setTitle(roleFormatted);
        return roleRepository.save(role);
    }

    public boolean deleteRoleById(Long id) {
        roleRepository.deleteById(id);
        return true;
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public Role getRoleByTitle(String title) {
        Optional<Role> role = roleRepository.findByTitle(title);
        if (role.isPresent()) {
            return role.get();
        } else {
            throw new ResourceNotFoundException("Пользовательской роли " + title + " не найдено");
        }
    }
}
