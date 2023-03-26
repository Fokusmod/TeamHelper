package ru.geekbrains.WowVendorTeamHelper.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.geekbrains.WowVendorTeamHelper.model.WowTeamRegion;

import java.util.Optional;

public interface WowTeamRegionRepository extends JpaRepository<WowTeamRegion, Long> {

    Optional<WowTeamRegion> findByTitle(String title);

}
