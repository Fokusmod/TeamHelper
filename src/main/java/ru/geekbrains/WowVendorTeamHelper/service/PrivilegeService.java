package ru.geekbrains.WowVendorTeamHelper.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.geekbrains.WowVendorTeamHelper.dto.PrivilegeDto;
import ru.geekbrains.WowVendorTeamHelper.exeptions.WWTHResourceNotFoundException;
import ru.geekbrains.WowVendorTeamHelper.model.Privilege;
import ru.geekbrains.WowVendorTeamHelper.repository.PrivilegeRepository;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PrivilegeService {

    private final PrivilegeRepository privilegeRepository;

    public Privilege saveOrUpdatePrivilege(PrivilegeDto privilegeDto) {
        Privilege privilege = new Privilege();
        privilege.setTitle(privilegeDto.getTitle());
        return privilegeRepository.save(privilege);
    }

    public boolean deletePrivilege(Long id) {
        privilegeRepository.deleteById(id);
        log.info("Удалена привилегия с идентификатором: " + id);
        return true;
    }

    public Privilege findById(Long privilegeId) {
        return privilegeRepository.findById(privilegeId).orElseThrow(() ->
                new WWTHResourceNotFoundException("Не удается найти привилегию с идентификатором: " + privilegeId));

    }

    public List<Privilege> getAllPrivilege() {
        return privilegeRepository.findAll();
    }
}
