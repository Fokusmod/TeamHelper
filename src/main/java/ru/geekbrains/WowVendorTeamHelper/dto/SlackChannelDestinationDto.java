package ru.geekbrains.WowVendorTeamHelper.dto;

import lombok.Data;
import ru.geekbrains.WowVendorTeamHelper.model.SlackChannelDestination;

@Data
public class SlackChannelDestinationDto {

    private Long id;

    private String title;

    public SlackChannelDestinationDto(SlackChannelDestination destination) {
        this.id = destination.getId();
        this.title = destination.getTitle();
    }
}
