package ru.geekbrains.WowVendorTeamHelper.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.geekbrains.WowVendorTeamHelper.dto.TypeRequest;
import ru.geekbrains.WowVendorTeamHelper.dto.WowEventTypeDTO;
import ru.geekbrains.WowVendorTeamHelper.service.WowEventTypeService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/eventType")
public class WowEventTypeController {

    private final WowEventTypeService wowEventTypeService;

    @GetMapping("/all")
    public List<WowEventTypeDTO> getAll () {
        return wowEventTypeService.getAllTypes().stream().map(WowEventTypeDTO::new).collect(Collectors.toList());
    }

    @PutMapping("/add/{title}")
    public ResponseEntity<WowEventTypeDTO> addType(@PathVariable String title) {
        return new ResponseEntity(wowEventTypeService.addType(title), HttpStatus.OK);
    }

    @PutMapping("/change")
    public ResponseEntity<WowEventTypeDTO> changeType(@RequestBody TypeRequest request) {
        return new ResponseEntity(wowEventTypeService.changeType(request), HttpStatus.OK);
    }

    @DeleteMapping("/id")
    public ResponseEntity<WowEventTypeDTO> deleteType(@PathVariable Long id) {
        return new ResponseEntity(wowEventTypeService.deleteType(id), HttpStatus.OK);
    }
}
