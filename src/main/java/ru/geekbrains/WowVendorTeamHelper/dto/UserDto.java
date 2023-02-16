package ru.geekbrains.WowVendorTeamHelper.dto;

import lombok.*;
import ru.geekbrains.WowVendorTeamHelper.model.Privilege;
import ru.geekbrains.WowVendorTeamHelper.model.User;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String status;
    private String password;
    private List<String> privileges;

    public UserDto(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.status = user.getStatus().getTitle();
        this.privileges = user.getPrivileges().stream().map(Privilege::getTitle).collect(Collectors.toList());
    }
}