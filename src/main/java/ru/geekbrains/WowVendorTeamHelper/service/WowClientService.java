package ru.geekbrains.WowVendorTeamHelper.service;

import liquibase.pro.packaged.F;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.geekbrains.WowVendorTeamHelper.model.MyMessage;
import ru.geekbrains.WowVendorTeamHelper.model.OrderStatus;
import ru.geekbrains.WowVendorTeamHelper.model.WowClient;
import ru.geekbrains.WowVendorTeamHelper.repository.WowClientRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WowClientService {

    private static final String STATUS_NEW = "NEW";
    private final OrderParser orderParser;
    private final WowClientRepository wowClientRepository;

    private final OrderStatusService orderStatusService;

    //TODO добавить метод удаления/изменения/добавления/получения client
    public void saveClient(WowClient wowClient) {
        wowClientRepository.save(wowClient);
    }

    public void getParseClients(MyMessage myMessage) {
        List<WowClient> list = orderParser.stringParser(myMessage);
        OrderStatus statusNew = orderStatusService.getOrderStatusByTitle(STATUS_NEW);
        for (WowClient wowClient : list) {
            if (wowClient.getOrderStatus() == null) {
                wowClient.setOrderStatus(statusNew);
            }
            saveClient(wowClient);
        }
    }


}
