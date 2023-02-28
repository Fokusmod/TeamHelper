package ru.geekbrains.WowVendorTeamHelper.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.geekbrains.WowVendorTeamHelper.dto.PrivilegeDto;
import ru.geekbrains.WowVendorTeamHelper.mapper.PrivilegeMapper;
import ru.geekbrains.WowVendorTeamHelper.service.PrivilegeService;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/privilege")
public class PrivilegeController {

    private final PrivilegeService privilegeService;
    private final PrivilegeMapper privilegeMapper;

    @GetMapping()
    public List<PrivilegeDto> getAllPrivilege() {
        return privilegeService.getAllPrivilege().stream().map(privilegeMapper::toDto).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public PrivilegeDto findById(@PathVariable Long id){
        return privilegeMapper.toDto(privilegeService.findById(id));
    }

    @PostMapping()
    public PrivilegeDto addPrivilege(@RequestBody PrivilegeDto privilegeDto) {
        return privilegeMapper.toDto(privilegeService.saveOrUpdatePrivilege(privilegeDto));
    }

    @DeleteMapping("/{id}")
    public boolean deletePrivilege(@PathVariable Long id) {
        return privilegeService.deletePrivilege(id);
    }

}
