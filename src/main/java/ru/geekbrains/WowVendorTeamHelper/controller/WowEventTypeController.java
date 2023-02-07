package ru.geekbrains.WowVendorTeamHelper.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.geekbrains.WowVendorTeamHelper.dto.WowEventTypeDTO;
import ru.geekbrains.WowVendorTeamHelper.model.WowEventType;
import ru.geekbrains.WowVendorTeamHelper.repository.WowEventTypeRepository;
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

}
