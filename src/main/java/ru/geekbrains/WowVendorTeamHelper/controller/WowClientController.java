package ru.geekbrains.WowVendorTeamHelper.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.geekbrains.WowVendorTeamHelper.dto.WowClientDto;
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
        return wowClientService.changeOrder(id,orderInfo).stream().map(WowClientDto::new).collect(Collectors.toList());
    }





}
