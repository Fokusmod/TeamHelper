package ru.geekbrains.WowVendorTeamHelper.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.geekbrains.WowVendorTeamHelper.dto.BundleRequest;
import ru.geekbrains.WowVendorTeamHelper.dto.BundleStageDto;
import ru.geekbrains.WowVendorTeamHelper.exeptions.ResourceNotFoundException;
import ru.geekbrains.WowVendorTeamHelper.model.Bundle;
import ru.geekbrains.WowVendorTeamHelper.model.BundleStage;
import ru.geekbrains.WowVendorTeamHelper.repository.BundleRepository;
import ru.geekbrains.WowVendorTeamHelper.repository.BundleStageRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
public class BundleService {

    private final BundleRepository bundleRepository;

    private final BundleStageRepository bundleStageRepository;

    public List<Bundle> getAllBundles() {
        return bundleRepository.findAll();
    }

    public List<BundleStage> getAllBundleStages() {
        return bundleStageRepository.findAll();
    }

    public Bundle findByTitle(String title) {
        return bundleRepository.findByTitle(title).orElseThrow(() -> new ResourceNotFoundException("Bundle '" + title + "' не найден"));
    }

    public void addNewBundle(BundleRequest bundleRequest) {
        Optional<Bundle> bundleInBD = bundleRepository.findByTitle(bundleRequest.getTitle());
        if (bundleInBD.isEmpty()) {
            List<String> list = bundleRequest.getBundleStages();
            Bundle bundle = new Bundle();
            bundle.setTitle(bundleRequest.getTitle());
            List<BundleStage> bundleStageList = new ArrayList<>();
            for (String s : list) {
                bundleStageList.add(createBundleStage(s));
            }
            bundle.setStages(bundleStageList);
            bundleRepository.save(bundle);
        } else {
            throw new ResourceNotFoundException("Bundle '" + bundleRequest.getTitle() + "' уже существует.");
        }
    }

    private BundleStage createBundleStage(String title) {
        Optional<BundleStage> bundleStage = bundleStageRepository.findByTitle(title);
        if (bundleStage.isEmpty()) {
            BundleStage stage = new BundleStage();
            stage.setTitle(title);
            return bundleStageRepository.save(stage);
        } else {
            return bundleStage.get();
        }
    }

    public Bundle findById(Long id) {
        return bundleRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Bundle с айди - '" + id + "' не найден."));
    }

    @Transactional
    public void deleteBundle(Long id) {
        Bundle bundle = findById(id);
        List<BundleStage> stages = bundle.getStages();
        bundleRepository.deleteById(id);
        deleteBundleStagesByBundle(stages);
    }

    private void deleteBundleStagesByBundle(List<BundleStage> stages) {
        List<Bundle> bundleList = getAllBundles();
        for (int i = 0; i < stages.size(); i++) {
            BundleStage stage = stages.get(i);
            boolean contains = false;
            for (int j = 0; j < bundleList.size(); j++) {
                List<BundleStage> bundleStageList = bundleList.get(j).getStages();
                if (bundleStageList.contains(stage)) {
                    contains = true;
                    break;
                }
            }
            if (!contains) {
                bundleStageRepository.delete(stage);
            }
        }

    }


}
