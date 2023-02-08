package ru.geekbrains.WowVendorTeamHelper.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.geekbrains.WowVendorTeamHelper.model.Privilege;
import ru.geekbrains.WowVendorTeamHelper.service.PrivilegeService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/privilege")
public class PrivilegeController {

    private final PrivilegeService privilegeService;

    @GetMapping()
    public List<Privilege> getAllPrivilege() {
        return privilegeService.getAllPrivilege();
    }

    @GetMapping("/{id}")
    public Privilege findById(@PathVariable Long id){
        return privilegeService.findById(id);
    }

    @PostMapping()
    public Privilege addPrivilege(@RequestBody Privilege privilege) {
        return privilegeService.saveOrUpdatePrivilege(privilege);
    }

    @DeleteMapping("/{id}")
    public boolean deletePrivilege(@PathVariable Long id) {
        return privilegeService.deletePrivilege(id);
    }

}
