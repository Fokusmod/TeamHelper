package ru.geekbrains.WowVendorTeamHelper.dto;

import lombok.Data;
import ru.geekbrains.WowVendorTeamHelper.model.Team;
import ru.geekbrains.WowVendorTeamHelper.model.WowEvent;
import ru.geekbrains.WowVendorTeamHelper.model.WowEventType;


@Data
public class WowEventDto {
    private Long id;
    private WowEventType wowEventType;
    private Team team;
    private String eventDate;
    private String startedAt;


    public WowEventDto(WowEvent wowEvent) {
        this.id = wowEvent.getId();
        this.wowEventType = wowEvent.getWowEventType();
        this.team = wowEvent.getTeam();
        this.eventDate = wowEvent.getEventDate();
        this.startedAt = wowEvent.getStartedAt();
    }


}
