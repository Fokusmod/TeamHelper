package ru.geekbrains.WowVendorTeamHelper.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;
import ru.geekbrains.WowVendorTeamHelper.dto.*;
import ru.geekbrains.WowVendorTeamHelper.model.SlackMessageInfo;
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
    public List<WowClientDto> getAllNewClients() {
        return wowClientService.getNewClients().stream().map(WowClientDto::new).collect(Collectors.toList());
    }

    @PutMapping("/{id}/comment")
    public WowClientDto setComments(@PathVariable Long id, @RequestBody String comments) {
        return new WowClientDto(wowClientService.setComments(id, comments));
    }

    @PutMapping("/{id}/order-info")
    public List<WowClientDto> parseOrderInfo(@PathVariable Long id, @RequestBody String orderInfo) {
        return wowClientService.changeOrder(id, orderInfo).stream().map(WowClientDto::new).collect(Collectors.toList());
    }

    @PostMapping("/{id}/client-stage")
    public void setClientStage(@PathVariable Long id, @RequestBody ClientBundleRequest request) {
        wowClientService.setClientStages(id, request);
    }

    @PostMapping
    public void addNewClient(@RequestBody String order) {
        SlackMessageInfo slackMessageInfo = new SlackMessageInfo();
        slackMessageInfo.setText(order);
        wowClientService.saveParseClients(slackMessageInfo);
    }

    @PostMapping("/find")
    public List<WowClientDto> findOrders(@RequestBody String request) {
        return wowClientService.findOrdersByOrderOrBattleTagOrId(request).stream().map(WowClientDto::new).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public WowClientDto getClientById(@PathVariable Long id) {
        return new WowClientDto(wowClientService.getClientById(id));
    }


}
