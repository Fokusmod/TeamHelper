package ru.geekbrains.WowVendorTeamHelper.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.geekbrains.WowVendorTeamHelper.model.Privilege;

@Repository
public interface PrivilegeRepository extends JpaRepository<Privilege, Long> {
}