package ru.geekbrains.WowVendorTeamHelper.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.geekbrains.WowVendorTeamHelper.model.WowEventType;

import java.util.Optional;

public interface WowEventTypeRepository extends JpaRepository<WowEventType,Long> {

    Optional<WowEventType> findByTitle (String title);
}
