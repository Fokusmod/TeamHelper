package ru.geekbrains.WowVendorTeamHelper.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.geekbrains.WowVendorTeamHelper.model.Bundle;

import java.util.Optional;

public interface BundleRepository extends JpaRepository<Bundle,Long> {

    Optional<Bundle> findByTitle(String title);

}
