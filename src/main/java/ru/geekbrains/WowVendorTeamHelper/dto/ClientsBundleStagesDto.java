package ru.geekbrains.WowVendorTeamHelper.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.geekbrains.WowVendorTeamHelper.model.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClientsBundleStagesDto {

    private Long id;

    private BundleStageDto bundleStage;

    private OrderStatus orderStatus;

    public ClientsBundleStagesDto(ClientsBundleStages clientsBundleStages) {
        this.bundleStage = new BundleStageDto(clientsBundleStages.getBundleStage());
        this.orderStatus = clientsBundleStages.getOrderStatus();

    }
}
