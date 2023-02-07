package ru.geekbrains.WowVendorTeamHelper.dto;

import lombok.Data;
import ru.geekbrains.WowVendorTeamHelper.model.WowEventType;
import ru.geekbrains.WowVendorTeamHelper.model.WowTeamRegion;

@Data
public class WowEventTypeDTO {

    private Long id;
    private String title;

    public WowEventTypeDTO(WowEventType wowEventType) {
        this.title = wowEventType.getTitle();
        this.id = wowEventType.getId();
    }

}
