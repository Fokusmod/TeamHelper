package ru.geekbrains.WowVendorTeamHelper.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.geekbrains.WowVendorTeamHelper.exeptions.ResourceNotFoundException;
import ru.geekbrains.WowVendorTeamHelper.model.WowEventType;
import ru.geekbrains.WowVendorTeamHelper.repository.WowEventTypeRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class WowEventTypeService {

    private final WowEventTypeRepository wowEventTypeRepository;


    public List<WowEventType> getAllTypes() {
        return wowEventTypeRepository.findAll();
    }

    public WowEventType getTypeByTitle(String title) {
        Optional<WowEventType> wowEventType = wowEventTypeRepository.findByTitle(title);
        if (wowEventType.isPresent()) {
            return wowEventType.get();
        } else {
            log.error("Тип события " + title + " не найден.");
            throw new ResourceNotFoundException("Тип события " + title + " не найден.");
        }
    }

    public void deleteType() {

    }

    public void changeType() {

    }

    public void addType() {

    }

}
