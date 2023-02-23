package ru.geekbrains.WowVendorTeamHelper.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.geekbrains.WowVendorTeamHelper.service.WowClientService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/clients")
public class WowClientController {

    private final WowClientService wowClientService;



    @GetMapping
    public void getAllClients () {

    }

}
