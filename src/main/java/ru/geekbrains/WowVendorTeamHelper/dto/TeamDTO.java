package ru.geekbrains.WowVendorTeamHelper.dto;

import lombok.Data;
import ru.geekbrains.WowVendorTeamHelper.model.Team;
import ru.geekbrains.WowVendorTeamHelper.model.WowTeamRegion;

@Data
public class TeamDTO {
    private String title;
    private WowTeamRegion region;


    public TeamDTO(Team team) {
        this.title = team.getTitle();
        this.region = team.getTeamRegion();
    }
}
