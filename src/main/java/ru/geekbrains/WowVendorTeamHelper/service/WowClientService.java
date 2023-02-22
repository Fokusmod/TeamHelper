package ru.geekbrains.WowVendorTeamHelper.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.geekbrains.WowVendorTeamHelper.model.MyMessage;
import ru.geekbrains.WowVendorTeamHelper.model.WowClient;
import ru.geekbrains.WowVendorTeamHelper.repository.WowClientRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WowClientService {

    private final OrderParser orderParser;
    private final WowClientRepository wowClientRepository;

    //TODO добавить метод удаления/изменения/добавления/получения client
    public void saveClient(WowClient wowClient) {
        wowClientRepository.save(wowClient);
    }

    public void getParseClients(MyMessage myMessage) {
        List<WowClient> list = orderParser.stringParser(myMessage);
        for (WowClient wowClient : list) {
            saveClient(wowClient);
        }
    }


}
