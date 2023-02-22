package ru.geekbrains.WowVendorTeamHelper.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BundleRequest {

    private String title;

    private List<String> bundleStages;


}
