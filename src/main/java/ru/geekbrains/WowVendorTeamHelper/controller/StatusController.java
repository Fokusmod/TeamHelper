package ru.geekbrains.WowVendorTeamHelper.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.geekbrains.WowVendorTeamHelper.model.Status;
import ru.geekbrains.WowVendorTeamHelper.service.StatusService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/status")
public class StatusController {

    private final StatusService statusService;

    @GetMapping()
    public List<Status> getAllStatus() {
        return statusService.getAllStatus();
    }

    @GetMapping("/get_status")
    public Status getStatus(@RequestParam (required = false) Long id, @RequestParam (required = false) String title){
        return statusService.getStatus(id, title);
    }

    @PostMapping()
    public Status addStatus(@RequestBody Status status) {
        return statusService.saveOrUpdateStatus(status);
    }

    @DeleteMapping("/{id}")
    public boolean deleteStatus(@PathVariable Long id) {
        return statusService.deleteStatus(id);
    }
}
