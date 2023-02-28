package ru.geekbrains.WowVendorTeamHelper.dto;

import lombok.Getter;
import lombok.Setter;
import ru.geekbrains.WowVendorTeamHelper.model.Bundle;

import java.util.List;
import java.util.stream.Collectors;

@Setter
@Getter
public class BundleDto {

    private Long id;

    private String title;

    private List<BundleStageDto> stages;


    public BundleDto(Bundle bundle) {
        this.id = bundle.getId();
        this.title = bundle.getTitle();
        this.stages = bundle.getStages().stream().map(BundleStageDto::new).collect(Collectors.toList());
    }
}
