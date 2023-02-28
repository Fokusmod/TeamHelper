package ru.geekbrains.WowVendorTeamHelper.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.geekbrains.WowVendorTeamHelper.dto.RoleDto;
import ru.geekbrains.WowVendorTeamHelper.model.Role;

@Component
@RequiredArgsConstructor
public class RoleMapper {

    public RoleDto toDto(Role role) {
        return RoleDto.builder()
                .id(role.getId())
                .title(role.getTitle())
                .build();
    }
}
