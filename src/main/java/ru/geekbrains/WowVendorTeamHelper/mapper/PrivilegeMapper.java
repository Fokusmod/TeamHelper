package ru.geekbrains.WowVendorTeamHelper.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.geekbrains.WowVendorTeamHelper.dto.PrivilegeDto;
import ru.geekbrains.WowVendorTeamHelper.model.Privilege;

@Component
@RequiredArgsConstructor
public class PrivilegeMapper {

    public PrivilegeDto toDto(Privilege privilege) {
        return PrivilegeDto.builder()
                .id(privilege.getId())
                .title(privilege.getTitle())
                .build();
    }
}
