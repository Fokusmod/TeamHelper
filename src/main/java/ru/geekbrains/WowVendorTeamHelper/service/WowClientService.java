package ru.geekbrains.WowVendorTeamHelper.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import ru.geekbrains.WowVendorTeamHelper.events.AddClientEvent;
import ru.geekbrains.WowVendorTeamHelper.model.MyMessage;
import ru.geekbrains.WowVendorTeamHelper.model.WowClient;
import ru.geekbrains.WowVendorTeamHelper.repository.WowClientRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WowClientService {

    private final OrderParser orderParser;
    private final WowClientRepository wowClientRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    //TODO добавить метод удаления/изменения/добавления/получения client
    public void saveClient(WowClient wowClient) {
        wowClientRepository.save(wowClient);
        applicationEventPublisher.publishEvent(new AddClientEvent(wowClient.getId()));
    }

    public void saveClientWithKafka() {
        applicationEventPublisher.publishEvent(new AddClientEvent(1L));
    }

    public void getParseClients(MyMessage myMessage) {
        List<WowClient> list = orderParser.stringParser(myMessage);
        for (WowClient wowClient : list) {
            saveClient(wowClient);
        }
    }


}
