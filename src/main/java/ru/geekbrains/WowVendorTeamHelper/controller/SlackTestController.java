package ru.geekbrains.WowVendorTeamHelper.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.geekbrains.WowVendorTeamHelper.model.MyMessage;
import ru.geekbrains.WowVendorTeamHelper.service.MessageService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SlackTestController {

    private final MessageService messageService;

    @GetMapping("/messages")
    public List<MyMessage> getAllMessage(){
        return messageService.getAllMessages();
    }

    @DeleteMapping("/deleteAll")
    public void deleteAllMessage() {
        messageService.deleteAllMessage();
    }


}
