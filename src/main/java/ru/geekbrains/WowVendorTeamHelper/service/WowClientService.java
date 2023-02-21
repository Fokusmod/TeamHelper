package ru.geekbrains.WowVendorTeamHelper.service;

import org.springframework.stereotype.Service;
import ru.geekbrains.WowVendorTeamHelper.model.WowClient;

import java.util.List;

@Service
public class WowClientService {


    //TODO доделать сущность Сlient
    //TODO добавить метод удаления/изменения/добавления/получения client
    public void saveClient(List<WowClient> list) {
        System.out.println(list);

    }

}
