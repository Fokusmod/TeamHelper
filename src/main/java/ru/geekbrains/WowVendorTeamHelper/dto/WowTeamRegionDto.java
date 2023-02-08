package ru.geekbrains.WowVendorTeamHelper.dto;

import lombok.Data;
import ru.geekbrains.WowVendorTeamHelper.model.WowTeamRegion;

@Data
public class WowTeamRegionDto {
    private Long id;
    private String title;

    public WowTeamRegionDto(WowTeamRegion wowTeamRegion) {
        this.title = wowTeamRegion.getTitle();
        this.id = wowTeamRegion.getId();
    }
}
