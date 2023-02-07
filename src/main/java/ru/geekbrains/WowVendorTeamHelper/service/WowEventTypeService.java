package ru.geekbrains.WowVendorTeamHelper.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.geekbrains.WowVendorTeamHelper.model.WowEventType;
import ru.geekbrains.WowVendorTeamHelper.repository.WowEventTypeRepository;

import java.util.List;
import java.util.Optional;

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
            throw new RuntimeException("Event type " + title + " not found");
        }
    }

    public void deleteType() {

    }

    public void changeType() {

    }

    public void addType() {

    }

}
