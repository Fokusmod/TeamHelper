package ru.geekbrains.WowVendorTeamHelper.dto;

import lombok.*;
import ru.geekbrains.WowVendorTeamHelper.model.WowEventStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class WowEventStatusDto {

    private Long id;

    private String title;

    public WowEventStatusDto(WowEventStatus wowEventStatus) {
        this.id = wowEventStatus.getId();
        this.title = wowEventStatus.getTitle();
    }
}
