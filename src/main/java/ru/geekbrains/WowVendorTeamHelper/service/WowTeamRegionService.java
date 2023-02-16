package ru.geekbrains.WowVendorTeamHelper.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.geekbrains.WowVendorTeamHelper.dto.RegionRequest;
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

    public WowTeamRegionDto deleteRegion(Long id) {
        Optional<WowTeamRegion> wowTeamRegion = repository.findById(id);
        if (wowTeamRegion.isPresent()) {
            WowTeamRegion region = wowTeamRegion.get();
            repository.delete(region);
            log.info("Регион: " + region.getTitle() + " удален.");
            return new WowTeamRegionDto(region);
        } else {
            log.error("Регион c id: " + id + " не найден.");
            throw new ResourceNotFoundException("Регион c id: " + id + " не найден.");
        }
    }

    @Transactional
    public WowTeamRegionDto changeRegion(RegionRequest request) {
        String newTitle = request.getNewRegion();
        Long id = request.getId();
        Optional<WowTeamRegion> wowTeamRegion = repository.findById(id);
        if (wowTeamRegion.isPresent()) {
            WowTeamRegion region = wowTeamRegion.get();
            region.setTitle(newTitle);
            repository.save(region);
            log.info("Региону c id: " + id + " изменено название на - " + newTitle);
            return new WowTeamRegionDto(region);
        } else {
            log.error("Регион c id: " + id + " не найден.");
            throw new ResourceNotFoundException("Регион c id: " + id + " не найден.");
        }
    }
}
