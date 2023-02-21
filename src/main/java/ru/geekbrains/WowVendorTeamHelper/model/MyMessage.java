package ru.geekbrains.WowVendorTeamHelper.model;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;
@Data
@RedisHash(value = "Messages", timeToLive = 3600L)
public class MyMessage implements Serializable {

    private String channel;
    @Id
    @Indexed
    private String ts;

    private String text;
    @Indexed
    private String channelMessageTs;


    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getTs() {
        return ts;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getChannelMessageTs() {
        return channelMessageTs;
    }

    public void setChannelMessageTs(String channelMessageTs) {
        this.channelMessageTs = channelMessageTs;
    }
}
