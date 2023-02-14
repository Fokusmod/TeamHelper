package ru.geekbrains.WowVendorTeamHelper.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.geekbrains.WowVendorTeamHelper.dto.SlackChannelDto;
import ru.geekbrains.WowVendorTeamHelper.dto.SlackChannelRequest;
import ru.geekbrains.WowVendorTeamHelper.model.SlackChannel;
import ru.geekbrains.WowVendorTeamHelper.service.SlackChannelService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/channels")
@RequiredArgsConstructor
public class SlackChannelController {

    private final SlackChannelService slackChannelService;

    @GetMapping("/all")
    public List<SlackChannelDto> getAllChannels() {
        return slackChannelService.getAllSlackChannels().stream().map(SlackChannelDto::new).collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<?> addingNewChannel(@RequestBody SlackChannelRequest request) {
        return slackChannelService.addingNewChannel(request);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteSlackChannel(@RequestBody SlackChannelRequest request) {
        return slackChannelService.deleteChannel(request);
    }

    @PutMapping
    public ResponseEntity<?> changeSlackChannelDestination(@RequestBody SlackChannelRequest request) {
        return slackChannelService.changeSlackChannelDestination(request);
    }

}
