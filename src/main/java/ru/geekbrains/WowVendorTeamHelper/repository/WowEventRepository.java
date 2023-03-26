package ru.geekbrains.WowVendorTeamHelper.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.geekbrains.WowVendorTeamHelper.model.WowEvent;

import java.util.List;

public interface WowEventRepository extends JpaRepository<WowEvent,Long> {

    List<WowEvent> findByTeamTitle(String string);

    List<WowEvent> findByWowEventTypeTitle(String string);

    List<WowEvent> findByTeamTitleAndWowEventTypeTitle(String team, String type);

    List<WowEvent> findByWowEventStatusTitle(String title);
}
