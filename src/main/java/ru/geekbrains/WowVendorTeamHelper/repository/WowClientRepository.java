package ru.geekbrains.WowVendorTeamHelper.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.geekbrains.WowVendorTeamHelper.model.OrderStatus;
import ru.geekbrains.WowVendorTeamHelper.model.WowClient;

import java.util.List;
import java.util.Optional;

public interface WowClientRepository extends JpaRepository<WowClient,Long> {

    List<WowClient> getWowClientByOrderStatus(OrderStatus status);
    Optional<WowClient> findByOrderCodeAndTs(String code,String ts);
    Optional<WowClient> findByOrderCode(String code);
    List<WowClient> getAllByTs(String ts);
}
