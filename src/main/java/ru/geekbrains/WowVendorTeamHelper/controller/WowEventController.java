package ru.geekbrains.WowVendorTeamHelper.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.geekbrains.WowVendorTeamHelper.dto.RequestEvents;
import ru.geekbrains.WowVendorTeamHelper.dto.ResponseEvents;
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
    public ResponseEntity<List<WowEventDto>> getAllEvents() {
        List<WowEventDto> list = wowEventService.getAllEvents().stream().map(WowEventDto::new).collect(Collectors.toList());
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @PostMapping(value = "/createList", consumes = "application/json")
    public ResponseEntity<List<ResponseEvents>> createEvents(@RequestBody List<RequestEvents> requestEvents) {
        List<ResponseEvents> list = wowEventService.createEvents(requestEvents);
        return new ResponseEntity<>(list, HttpStatus.OK);
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
    public ResponseEntity<WowEventDto> changeEvent(@PathVariable Long id, @RequestBody List<RequestEvents> requestEvents) {
        WowEventDto dto = wowEventService.changeById(id,requestEvents);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteEvent(@PathVariable Long id) {
        wowEventService.deleteEvent(id);
    }
}
