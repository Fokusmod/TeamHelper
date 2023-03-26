package ru.geekbrains.WowVendorTeamHelper.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.geekbrains.WowVendorTeamHelper.dto.*;
import ru.geekbrains.WowVendorTeamHelper.model.WowEvent;
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

    @GetMapping
    public ResponseEntity<List<WowEventDto>> getAllEvents() {
        List<WowEventDto> list = wowEventService.getAllEvents().stream().map(WowEventDto::new).collect(Collectors.toList());
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public WowEventResponse getEventById(@PathVariable Long id) {
        return new WowEventResponse(wowEventService.getById(id));
    }


    @GetMapping("/with-clients")
    public ResponseEntity<List<WowEventResponse>> getAllEventsWithClients() {
        List<WowEventResponse> list = wowEventService.getAllEvents().stream().map(WowEventResponse::new).collect(Collectors.toList());
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @PostMapping("/team/with-clients")
    public ResponseEntity<List<WowEventResponse>> getAllEventsWithClients(@RequestBody List<TeamDTO> teams) {
        List<WowEventResponse> list = wowEventService.getEventsByArrayTeam(teams).stream().map(WowEventResponse::new).collect(Collectors.toList());
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @PostMapping(value = "/create-list", consumes = "application/json")
    public ResponseEntity<List<ResponseEvents>> createEvents(@RequestBody List<RequestEvents> requestEvents) {
        List<ResponseEvents> list = wowEventService.createEvents(requestEvents);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @PostMapping("/create-text")
    public void createEventsText(@RequestBody String strings) {
        List<RequestEvents> list = parseEventService.parseRequest(strings);
        wowEventService.createEvents(list);
    }

    @GetMapping("/by-team/{team}")
    public List<WowEventDto> getByTeamTitle(@PathVariable String team) {
        return wowEventService.getByTeamTitle(team).stream().map(WowEventDto::new).collect(Collectors.toList());
    }

    @GetMapping("/by-type/{type}")
    public List<WowEventDto> getByEventType(@PathVariable String type) {
        return wowEventService.getByEventType(type).stream().map(WowEventDto::new).collect(Collectors.toList());
    }

    @GetMapping("/filter/{team}/{type}")
    public List<WowEventDto> getFilterSchedule(@PathVariable String team, @PathVariable String type) {
        return wowEventService.getByTeamAndType(team, type).stream().map(WowEventDto::new).collect(Collectors.toList());
    }

    @PutMapping (value = "/change-list/{id}")
    public ResponseEntity<WowEventDto> changeEvent(@PathVariable Long id, @RequestBody List<RequestEvents> requestEvents) {
        WowEventDto dto = wowEventService.changeById(id,requestEvents);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PostMapping("/active")
    public List<WowEventResponse> getActiveEvents(@RequestBody String message){
       return wowEventService.getActiveEvents(message).stream().map(WowEventResponse::new).collect(Collectors.toList());
    }

    @GetMapping("/active")
    public List<WowEventResponse> getStartActiveEvents() {
        return wowEventService.getStartActiveEvents().stream().map(WowEventResponse::new).collect(Collectors.toList());
    }

    @DeleteMapping("/delete/{id}")
    public void deleteEvent(@PathVariable Long id) {
        wowEventService.deleteEvent(id);
    }

    @PostMapping("/{eventId}/{clientId}")
    public void addClientInEvent(@PathVariable Long eventId, @PathVariable Long clientId) {
        wowEventService.addClientInEvent(eventId,clientId);
    }
}
