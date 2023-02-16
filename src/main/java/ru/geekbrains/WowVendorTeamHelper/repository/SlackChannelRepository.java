package ru.geekbrains.WowVendorTeamHelper.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.geekbrains.WowVendorTeamHelper.model.SlackChannel;

import java.util.Optional;

public interface SlackChannelRepository extends JpaRepository<SlackChannel,Long> {

    Optional<SlackChannel> findByChannelId(String channelId);
}
