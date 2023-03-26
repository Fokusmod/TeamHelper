package ru.geekbrains.WowVendorTeamHelper.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.geekbrains.WowVendorTeamHelper.dto.BundleDto;
import ru.geekbrains.WowVendorTeamHelper.dto.BundleRequest;
import ru.geekbrains.WowVendorTeamHelper.dto.BundleResponse;
import ru.geekbrains.WowVendorTeamHelper.service.BundleService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bundle")
public class BundleController {

    private final BundleService bundleService;

    @GetMapping
    public BundleResponse getAllBundles(){
        List<BundleDto> list = bundleService.getAllBundles().stream().map(BundleDto::new).collect(Collectors.toList());
        return new BundleResponse(list);
    }

    @PostMapping
    public void addNewBundle(@RequestBody BundleRequest bundleRequest) {
        bundleService.addNewBundle(bundleRequest);

    }

    @DeleteMapping("/{id}")
    public void deleteBundle(@PathVariable Long id){
        bundleService.deleteBundle(id);
    }




}
