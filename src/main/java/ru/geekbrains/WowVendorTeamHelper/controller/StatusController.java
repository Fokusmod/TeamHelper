package ru.geekbrains.WowVendorTeamHelper.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.geekbrains.WowVendorTeamHelper.dto.StatusDto;
import ru.geekbrains.WowVendorTeamHelper.mapper.StatusMapper;
import ru.geekbrains.WowVendorTeamHelper.service.StatusService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/status")
public class StatusController {

    private final StatusService statusService;
    private final StatusMapper statusMapper;

    @GetMapping()
    public List<StatusDto> getAllStatus() {
        return statusService.getAllStatus().stream().map(statusMapper::toDto).collect(Collectors.toList());
    }

    @GetMapping("/get_by_id/{id}")
    public StatusDto getStatusById(@PathVariable Long id){
        return statusMapper.toDto(statusService.findById(id));
    }

    @GetMapping("/get_by_title/{title}")
    public StatusDto getStatusByTitle(@PathVariable String title){
        return statusMapper.toDto(statusService.findByTitle(title));
    }

    @PostMapping()
    public StatusDto addStatus(@RequestBody StatusDto statusDto) {
        return statusMapper.toDto(statusService.saveStatus(statusDto));
    }

    @DeleteMapping("/{id}")
    public boolean deleteStatus(@PathVariable Long id) {
        return statusService.deleteStatus(id);
    }
}
