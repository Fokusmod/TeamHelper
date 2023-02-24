package ru.geekbrains.WowVendorTeamHelper.listeners;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.geekbrains.WowVendorTeamHelper.dto.WowClientDto;
import ru.geekbrains.WowVendorTeamHelper.events.AddClientEvent;
import ru.geekbrains.WowVendorTeamHelper.repository.WowClientRepository;

import java.util.Collections;
import java.util.List;

@Component
public class KafkaListeners {

    @Qualifier(value = "Kafka")
    @Autowired
    private KafkaTemplate<Long, List<WowClientDto>> kafkaTemplate;

    @Autowired
    private WowClientRepository wowClientRepository;

    @EventListener
    public void onAddClientEvent(AddClientEvent event) {
        //FIXME Включить при наполненной базе клиентов
//        List<WowClient> wowClients = wowClientRepository.findAll();
//        List<WowClientDto> dtoList = WowClient.list(wowClients);
//        kafkaTemplate.send("ClientList", dtoList);

        //Это тестовый код
        //FIXME Выключить после включения верхнего кода
        kafkaTemplate.send("ClientList", Collections.singletonList(
                WowClientDto.builder()
                .id(1L)
                .orderCode("orderCode")
                .battleTag("tag")
                .fraction("fraction")
                .realm("realm")
                .nickname("nickname")
                .orderDateTime("orderDateTime")
                .characterClass("characterClass")
                .boostMode("boostMode")
                .playingType("playingType")
                .region("region")
                .service("service")
                .game("game")
                .specificBosses("specificBosses")
                .armoryLink("armoryLink")
                .noParseInfo("noParseInfo")
                .build())
        );
        kafkaTemplate.flush();
    }

}
