package ru.geekbrains.WowVendorTeamHelper.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.geekbrains.WowVendorTeamHelper.model.MyMessage;

import java.util.Optional;
@Repository
@Qualifier("MySlackMessages")
public interface SlackMessageRepository extends CrudRepository<MyMessage,String> {

    Optional<MyMessage> findByTs(String ts);

    Optional<MyMessage> findByChannelMessageTs(String channelMessageTs);

}
