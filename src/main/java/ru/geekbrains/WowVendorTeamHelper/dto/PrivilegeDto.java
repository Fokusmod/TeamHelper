package ru.geekbrains.WowVendorTeamHelper.dto;

import lombok.Getter;
import lombok.Setter;
import ru.geekbrains.WowVendorTeamHelper.model.Privilege;

@Getter
@Setter
public class PrivilegeDto {

    private Long id;
    private String title;

    public PrivilegeDto(Privilege privilege) {
        this.id = privilege.getId();
        this.title = privilege.getTitle();
    }
}
