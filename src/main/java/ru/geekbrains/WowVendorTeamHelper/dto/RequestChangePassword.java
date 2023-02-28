package ru.geekbrains.WowVendorTeamHelper.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestChangePassword {

    private String oldPassword;
    private String newPassword;
    private String email;
}
