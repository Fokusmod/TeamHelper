package ru.geekbrains.WowVendorTeamHelper.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPrivilegeRequest {

    private Long userId;
    private Long privilegeId;
}
