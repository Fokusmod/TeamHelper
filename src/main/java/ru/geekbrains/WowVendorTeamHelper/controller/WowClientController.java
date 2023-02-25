package ru.geekbrains.WowVendorTeamHelper.controller;

import com.slack.api.socket_mode.request.HelloMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.geekbrains.WowVendorTeamHelper.config.WebSocketConfig;
import ru.geekbrains.WowVendorTeamHelper.dto.EventMessage;
import ru.geekbrains.WowVendorTeamHelper.dto.WowClientDto;
import ru.geekbrains.WowVendorTeamHelper.model.WowClient;
import ru.geekbrains.WowVendorTeamHelper.service.WowClientService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/clients")
public class WowClientController {

    private final WowClientService wowClientService;

    private final WebSocketConfig webSocketConfig;


    @GetMapping
    public List<WowClientDto> getAllClients () {
        return wowClientService.getAllClients().stream().map(WowClientDto::new).collect(Collectors.toList());
    }




}
