package ru.geekbrains.WowVendorTeamHelper.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.geekbrains.WowVendorTeamHelper.model.Team;

import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team,Long> {

    Optional<Team> getByTitle(String title);
}
