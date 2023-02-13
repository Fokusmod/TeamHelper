package ru.geekbrains.WowVendorTeamHelper.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.geekbrains.WowVendorTeamHelper.dto.WowTeamRegionDto;
import ru.geekbrains.WowVendorTeamHelper.exeptions.ResourceNotFoundException;
import ru.geekbrains.WowVendorTeamHelper.model.WowTeamRegion;
import ru.geekbrains.WowVendorTeamHelper.repository.WowTeamRegionRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class WowTeamRegionService {

    private final WowTeamRegionRepository repository;


    public List<WowTeamRegion> getAllRegions() {
       return repository.findAll();
    }

    public void addRegion(String title) {
        WowTeamRegion wowTeamRegion = new WowTeamRegion();
        wowTeamRegion.setTitle(title);
        repository.save(wowTeamRegion);
    }

    public WowTeamRegionDto deleteRegion(String title) {
        Optional<WowTeamRegion> wowTeamRegion = repository.findByTitle(title);
        if (wowTeamRegion.isPresent()) {
            WowTeamRegion region = wowTeamRegion.get();
            repository.delete(region);
            log.info("Регион " + title + " удален.");
            return new WowTeamRegionDto(region);
        } else {
            log.error("Регион " + title + " не найден.");
            throw new ResourceNotFoundException("Регион " + title + " не найден.");
        }
    }

    @Transactional
    public WowTeamRegionDto changeRegion(String oldTitle, String newTitle) {
        Optional<WowTeamRegion> wowTeamRegion = repository.findByTitle(oldTitle);
        if (wowTeamRegion.isPresent()) {
            WowTeamRegion region = wowTeamRegion.get();
            region.setTitle(newTitle);
            repository.save(region);
            log.info("Регион " + oldTitle + " изменен на " + newTitle);
            return new WowTeamRegionDto(region);
        } else {
            log.error("Регион " + oldTitle + " не найден.");
            throw new ResourceNotFoundException("Регион " + oldTitle + " не найден.");
        }
    }
}
