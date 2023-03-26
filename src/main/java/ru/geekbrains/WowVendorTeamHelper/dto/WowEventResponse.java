package ru.geekbrains.WowVendorTeamHelper.dto;

import lombok.*;
import ru.geekbrains.WowVendorTeamHelper.model.Team;
import ru.geekbrains.WowVendorTeamHelper.model.WowClient;
import ru.geekbrains.WowVendorTeamHelper.model.WowEvent;
import ru.geekbrains.WowVendorTeamHelper.model.WowEventType;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class WowEventResponse {

    private Long id;
    private WowEventType wowEventType;
    private Team team;
    private String eventDate;
    private String startedAt;
    private WowEventStatusDto wowEventStatusDto;
    private List<WowClientDto> clientList;


    public WowEventResponse(WowEvent wowEvent) {
        this.id = wowEvent.getId();
        this.wowEventType = wowEvent.getWowEventType();
        this.team = wowEvent.getTeam();
        this.eventDate = wowEvent.getEventDate();
        this.startedAt = wowEvent.getStartedAt();
        this.wowEventStatusDto = new WowEventStatusDto(wowEvent.getWowEventStatus());
        this.clientList = wowEvent.getClientList().stream().map(WowClientDto::new).collect(Collectors.toList());
    }
}
