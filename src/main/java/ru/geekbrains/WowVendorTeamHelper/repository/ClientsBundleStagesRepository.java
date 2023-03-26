package ru.geekbrains.WowVendorTeamHelper.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.geekbrains.WowVendorTeamHelper.model.ClientsBundleStages;

import java.util.List;
import java.util.Optional;

public interface ClientsBundleStagesRepository extends JpaRepository<ClientsBundleStages,Long>{

    List<ClientsBundleStages>findByClientId(Long id);
}
