package ru.geekbrains.WowVendorTeamHelper.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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



    @GetMapping
    public List<WowClientDto> getAllClients () {
        return wowClientService.getAllClients().stream().map(WowClientDto::new).collect(Collectors.toList());
    }

}
