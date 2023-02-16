package ru.geekbrains.WowVendorTeamHelper.dto;

import lombok.Data;
import ru.geekbrains.WowVendorTeamHelper.model.SlackChannel;
import ru.geekbrains.WowVendorTeamHelper.model.SlackChannelDestination;

import java.util.Set;
import java.util.stream.Collectors;

@Data
public class SlackChannelDto {

    private Long id;

    private String channelId;

    private String title;

    private Set<SlackChannelDestinationDto> slackChannelDestination;

    public SlackChannelDto(SlackChannel slackChannel) {
        this.id = slackChannel.getId();
        this.channelId = slackChannel.getChannelId();
        this.title = slackChannel.getTitle();
        this.slackChannelDestination = slackChannel.getSlackChannelDestination().stream().map(SlackChannelDestinationDto::new).collect(Collectors.toSet());
    }
}
