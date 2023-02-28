package ru.geekbrains.WowVendorTeamHelper.model;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(value = "Messages", timeToLive = 3600L)
public class SlackMessageInfo implements Serializable {

    private String channel;
    @Id
    @Indexed
    private String ts;

    private String text;
    @Indexed
    private String otherChannelMessageTs;

}
