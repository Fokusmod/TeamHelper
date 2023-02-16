package ru.geekbrains.WowVendorTeamHelper.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegionRequest {

    private Long id;
    private String oldRegion;
    private String newRegion;

    public RegionRequest() {
    }

    public RegionRequest(Long id, String oldRegion, String newRegion) {
        this.id = id;
        this.oldRegion = oldRegion;
        this.newRegion = newRegion;
    }
}
