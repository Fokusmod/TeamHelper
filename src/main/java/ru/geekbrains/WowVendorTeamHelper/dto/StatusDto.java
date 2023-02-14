package ru.geekbrains.WowVendorTeamHelper.dto;

import lombok.Getter;
import lombok.Setter;
import ru.geekbrains.WowVendorTeamHelper.model.Status;

@Getter
@Setter
public class StatusDto {
    private Long id;
    private String title;

    public StatusDto(Status status) {
        this.id = status.getId();
        this.title = status.getTitle();
    }
}
