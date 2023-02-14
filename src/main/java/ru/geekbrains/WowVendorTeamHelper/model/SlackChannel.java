package ru.geekbrains.WowVendorTeamHelper.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.Set;

@Data
@Entity
@Table(name = "slack_channels")
public class SlackChannel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "channel_id")
    private String channelId;

    @Column(name = "title")
    private String title;

    @ManyToMany
    @Fetch(FetchMode.JOIN)
    @JoinTable(name = "channels_and_destinations",
            joinColumns = @JoinColumn(name = "slack_channel_id"),
            inverseJoinColumns = @JoinColumn(name = "destination_id"))
    private Set<SlackChannelDestination> slackChannelDestination;
}