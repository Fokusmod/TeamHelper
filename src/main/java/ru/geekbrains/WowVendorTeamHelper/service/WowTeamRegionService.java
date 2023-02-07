package ru.geekbrains.WowVendorTeamHelper.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.geekbrains.WowVendorTeamHelper.exeptions.TeamNotFoundException;
import ru.geekbrains.WowVendorTeamHelper.exeptions.WowTeamRegionNotFoundException;
import ru.geekbrains.WowVendorTeamHelper.model.WowTeamRegion;
import ru.geekbrains.WowVendorTeamHelper.repository.WowTeamRegionRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WowTeamRegionService {

    private final WowTeamRegionRepository repository;


    public List<WowTeamRegion> getAllRegions() {
       return repository.findAll();
    }

    public void addRegion (String title) {
        WowTeamRegion wowTeamRegion = new WowTeamRegion();
        wowTeamRegion.setTitle(title);
        repository.save(wowTeamRegion);
    }

    public void deleteRegion (String title) {
        Optional<WowTeamRegion> wowTeamRegion = repository.findByTitle(title);
        if (wowTeamRegion.isPresent()) {
            repository.delete(wowTeamRegion.get());
        } else {
           throw new WowTeamRegionNotFoundException("Регион " + title + " не найден.");
        }
    }


}
