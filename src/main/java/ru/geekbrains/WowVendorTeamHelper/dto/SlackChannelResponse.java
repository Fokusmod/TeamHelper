package ru.geekbrains.WowVendorTeamHelper.dto;

import lombok.Data;

@Data
public class SlackChannelResponse {

    private String title;

    private String channelId;

    private String destination;
}
