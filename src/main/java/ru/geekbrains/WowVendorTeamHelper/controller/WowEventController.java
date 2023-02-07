package ru.geekbrains.WowVendorTeamHelper.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.geekbrains.WowVendorTeamHelper.dto.RequestEvents;
import ru.geekbrains.WowVendorTeamHelper.dto.WowEventDto;
import ru.geekbrains.WowVendorTeamHelper.service.ParseEventService;
import ru.geekbrains.WowVendorTeamHelper.service.WowEventService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")

public class WowEventController {

    private final WowEventService wowEventService;

    private final ParseEventService parseEventService;

    @GetMapping("/all")
    public List<WowEventDto> getAllEvents() {
        return wowEventService.getAllEvents().stream().map(WowEventDto::new).collect(Collectors.toList());
    }

    @PostMapping(value = "/createList", consumes = "application/json")
    public void createEvents(@RequestBody List<RequestEvents> requestEvents) {
        wowEventService.createEvents(requestEvents);
    }

    @PostMapping("/createText")
    public void createEventsText(@RequestBody String strings) {
        List<RequestEvents> list = parseEventService.parseRequest(strings);
        wowEventService.createEvents(list);
    }

    @GetMapping("/byTeam/{team}")
    public List<WowEventDto> getByTeamTitle(@PathVariable String team) {
        return wowEventService.getByTeamTitle(team).stream().map(WowEventDto::new).collect(Collectors.toList());
    }

    @GetMapping("/byType/{type}")
    public List<WowEventDto> getByEventType(@PathVariable String type) {
        return wowEventService.getByEventType(type).stream().map(WowEventDto::new).collect(Collectors.toList());
    }

    @GetMapping("/filter/{team}/{type}")
    public List<WowEventDto> getFilterSchedule(@PathVariable String team, @PathVariable String type) {
        return wowEventService.getByTeamAndType(team, type).stream().map(WowEventDto::new).collect(Collectors.toList());
    }

    @PutMapping (value = "/changeList/{id}")
    public void changeEvent(@PathVariable Long id, @RequestBody List<RequestEvents> requestEvents) {
        wowEventService.changeById(id,requestEvents);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteEvent(@PathVariable Long id) {
        wowEventService.deleteEvent(id);
    }






}
