package ru.geekbrains.WowVendorTeamHelper.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.geekbrains.WowVendorTeamHelper.model.SlackMessageInfo;

import java.util.Optional;
@Repository
@Qualifier("MySlackMessages")
public interface SlackMessageRepository extends CrudRepository<SlackMessageInfo,String> {

    Optional<SlackMessageInfo> findByTs(String ts);

    Optional<SlackMessageInfo> findByChannelMessageTs(String channelMessageTs);

}
