package ru.geekbrains.WowVendorTeamHelper.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.geekbrains.WowVendorTeamHelper.model.BundleStage;

import java.util.Optional;

public interface BundleStageRepository extends JpaRepository<BundleStage,Long> {

    Optional<BundleStage> findByTitle(String title);

    void deleteByTitle(String title);

}
