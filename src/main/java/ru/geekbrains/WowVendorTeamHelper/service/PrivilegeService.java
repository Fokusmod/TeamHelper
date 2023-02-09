package ru.geekbrains.WowVendorTeamHelper.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.geekbrains.WowVendorTeamHelper.exeptions.ResourceNotFoundException;
import ru.geekbrains.WowVendorTeamHelper.model.Privilege;
import ru.geekbrains.WowVendorTeamHelper.repository.PrivilegeRepository;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PrivilegeService {

    private final PrivilegeRepository privilegeRepository;

    public Privilege saveOrUpdatePrivilege(Privilege privilege) {
        return privilegeRepository.save(privilege);
    }

    public boolean deletePrivilege(Long id) {
        privilegeRepository.deleteById(id);
        log.info("Removed privilege with id: " + id);
        return true;
    }

    public Privilege findById(Long privilegeId) {
        return privilegeRepository.findById(privilegeId).orElseThrow(() ->
                new ResourceNotFoundException("Unable to find privilege with id: " + privilegeId));
    }

    public List<Privilege> getAllPrivilege() {
        return privilegeRepository.findAll();
    }
}
