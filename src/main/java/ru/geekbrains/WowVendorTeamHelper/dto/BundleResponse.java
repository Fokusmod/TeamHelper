package ru.geekbrains.WowVendorTeamHelper.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BundleResponse {

   private List<BundleDto> bundleDtoList;

    public BundleResponse(List<BundleDto> bundles) {
        this.bundleDtoList = bundles;
    }
}
