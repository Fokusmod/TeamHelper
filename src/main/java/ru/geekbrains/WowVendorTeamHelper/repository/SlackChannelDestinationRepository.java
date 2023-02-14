package ru.geekbrains.WowVendorTeamHelper.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.geekbrains.WowVendorTeamHelper.model.SlackChannelDestination;

import java.util.Optional;

public interface SlackChannelDestinationRepository extends JpaRepository<SlackChannelDestination,Long> {

    Optional<SlackChannelDestination> findByTitle(String title);

}
