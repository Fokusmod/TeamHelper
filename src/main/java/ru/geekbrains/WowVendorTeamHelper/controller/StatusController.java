package ru.geekbrains.WowVendorTeamHelper.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.geekbrains.WowVendorTeamHelper.dto.StatusDto;
import ru.geekbrains.WowVendorTeamHelper.service.StatusService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/status")
public class StatusController {

    private final StatusService statusService;

    @GetMapping()
    public List<StatusDto> getAllStatus() {
        return statusService.getAllStatus().stream().map(StatusDto::new).collect(Collectors.toList());
    }

    @GetMapping("/get_status")
    public StatusDto getStatus(@RequestParam (required = false) Long id, @RequestParam (required = false) String title){
        return new StatusDto(statusService.getStatus(id, title));
    }

    @PostMapping()
    public StatusDto addStatus(@RequestBody StatusDto statusDto) {
        return new StatusDto(statusService.saveOrUpdateStatus(statusDto));
    }

    @DeleteMapping("/{id}")
    public boolean deleteStatus(@PathVariable Long id) {
        return statusService.deleteStatus(id);
    }
}
