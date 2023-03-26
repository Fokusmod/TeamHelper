package ru.geekbrains.WowVendorTeamHelper.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ClientBundleRequest {

    private Long id;

    private String title;

    private List<BundleStageDto> stages;

}
