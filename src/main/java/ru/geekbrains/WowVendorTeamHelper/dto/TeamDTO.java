package ru.geekbrains.WowVendorTeamHelper.dto;

import lombok.*;
import ru.geekbrains.WowVendorTeamHelper.model.Team;
import ru.geekbrains.WowVendorTeamHelper.model.WowTeamRegion;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TeamDTO {
    private String title;
    private WowTeamRegion region;


    public TeamDTO(Team team) {
        this.title = team.getTitle();
        this.region = team.getTeamRegion();
    }
}
