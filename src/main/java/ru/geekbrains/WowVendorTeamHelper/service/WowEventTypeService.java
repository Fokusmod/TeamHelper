package ru.geekbrains.WowVendorTeamHelper.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.geekbrains.WowVendorTeamHelper.dto.TypeRequest;
import ru.geekbrains.WowVendorTeamHelper.dto.WowEventTypeDTO;
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
        Optional<WowEventType> optional = wowEventTypeRepository.findByTitle(title);
        if (optional.isEmpty()) {
            log.error("Тип события " + title + " не найден.");
            throw new ResourceNotFoundException("Тип события " + title + " не найден.");
        }
        return optional.get();
    }

    @Transactional
    public WowEventTypeDTO deleteType(Long id) {
        Optional<WowEventType> optional = wowEventTypeRepository.findById(id);
        if (optional.isEmpty()) {
            log.error("Тип эвента c id: " + id + " не найден.");
            throw new ResourceNotFoundException("Тип эвента c id: " + id + " не найден.");
        }
        WowEventType type = optional.get();
        String title = type.getTitle();
        wowEventTypeRepository.delete(type);
        log.info("Тип эвента " + title + " удален.");
        return WowEventType.makeDto(type);
    }

    @Transactional
    public WowEventTypeDTO changeType(TypeRequest request) {
        Long id = request.getId();
        String newTitle = request.getNewTitle();
        Optional<WowEventType> optional = wowEventTypeRepository.findById(id);
        if (optional.isEmpty()) {
            log.error("Тип эвента " + newTitle + " не найден.");
            throw new ResourceNotFoundException("Тип эвента " + newTitle + " не найден.");
        }
        WowEventType type = optional.get();
        String oldTitle = type.getTitle();
        type.setTitle(newTitle);
        wowEventTypeRepository.save(type);
        log.info("Тип эвента изменен с" + oldTitle + " на " + newTitle + ".");
        return WowEventType.makeDto(type);
    }

    @Transactional
    public WowEventTypeDTO addType(String title) {
        WowEventType type = new WowEventType();
        type.setTitle(title);
        WowEventType savedType = wowEventTypeRepository.save(type);
        log.info("Добавлен тип эвента: " + type.getTitle() + ".");
        return WowEventType.makeDto(savedType);
    }

}
