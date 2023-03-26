package ru.geekbrains.WowVendorTeamHelper.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.geekbrains.WowVendorTeamHelper.model.WowEventStatus;

import java.util.Optional;

public interface WowEventStatusRepository extends JpaRepository<WowEventStatus,Long> {

    Optional<WowEventStatus> findByTitle(String title);
}
