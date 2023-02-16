package ru.geekbrains.WowVendorTeamHelper.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.geekbrains.WowVendorTeamHelper.dto.RegionRequest;
import ru.geekbrains.WowVendorTeamHelper.dto.TeamDTO;
import ru.geekbrains.WowVendorTeamHelper.dto.WowTeamRegionDto;
import ru.geekbrains.WowVendorTeamHelper.service.TeamService;
import ru.geekbrains.WowVendorTeamHelper.service.WowTeamRegionService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/teams")
public class TeamController {

    private final TeamService teamService;
    private final WowTeamRegionService regionService;


    @GetMapping("/all")
    public List<TeamDTO> getTeams (){
        return teamService.getAllTeams().stream().map(TeamDTO::new).collect(Collectors.toList());
    }

    @GetMapping("/wowRegions")
    public List<WowTeamRegionDto> getTeamsRegions () {
        return regionService.getAllRegions().stream().map(WowTeamRegionDto::new).collect(Collectors.toList());
    }

    @PutMapping("/wowRegions")
    public ResponseEntity<WowTeamRegionDto> changeRegion(@RequestBody RegionRequest request) {
        WowTeamRegionDto dto = regionService.changeRegion(request);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @DeleteMapping("/wowRegions/{id}")
    public ResponseEntity<WowTeamRegionDto> getTeamsRegions (@PathVariable Long id) {
        WowTeamRegionDto dto = regionService.deleteRegion(id);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }
}
