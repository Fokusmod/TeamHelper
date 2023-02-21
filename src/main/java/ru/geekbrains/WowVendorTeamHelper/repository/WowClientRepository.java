package ru.geekbrains.WowVendorTeamHelper.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.geekbrains.WowVendorTeamHelper.model.WowClient;

public interface WowClientRepository extends JpaRepository<WowClient,Long> {


}
