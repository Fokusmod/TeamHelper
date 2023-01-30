package ru.geekbrains.WowVendorTeamHelper.dto;

import lombok.Data;
import ru.geekbrains.WowVendorTeamHelper.model.Team;

@Data
public class TeamDTO {

    private String title;


    public TeamDTO(Team team) {

        this.title = team.getTitle();

    }
}
