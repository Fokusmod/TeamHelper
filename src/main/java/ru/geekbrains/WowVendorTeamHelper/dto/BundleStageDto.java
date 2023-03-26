package ru.geekbrains.WowVendorTeamHelper.dto;

import lombok.*;
import ru.geekbrains.WowVendorTeamHelper.model.BundleStage;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BundleStageDto {

    private Long id;

    private String title;

    public BundleStageDto(BundleStage bundleStage) {
        this.id = bundleStage.getId();
        this.title = bundleStage.getTitle();
    }
}
