package ru.geekbrains.WowVendorTeamHelper.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.geekbrains.WowVendorTeamHelper.model.SlackMessageInfo;
import ru.geekbrains.WowVendorTeamHelper.service.MessageService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/message")
public class SlackTestController {

    private final MessageService messageService;

    @GetMapping("/all")
    public List<SlackMessageInfo> getAllMessage(){
        return messageService.getAllMessages();
    }

    @DeleteMapping("/all")
    public void deleteAllMessage() {
        messageService.deleteAllMessage();
    }


}
