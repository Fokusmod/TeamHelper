package ru.geekbrains.WowVendorTeamHelper.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import ru.geekbrains.WowVendorTeamHelper.model.BundleStage;

@Getter
@Setter
public class BundleStageDto {

    private Long id;

    private String title;

    public BundleStageDto(BundleStage bundleStage) {
        this.id = bundleStage.getId();
        this.title = bundleStage.getTitle();
    }
}
