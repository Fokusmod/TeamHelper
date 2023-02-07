package ru.geekbrains.WowVendorTeamHelper.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.geekbrains.WowVendorTeamHelper.dto.TeamDTO;
import ru.geekbrains.WowVendorTeamHelper.dto.WowTeamRegionDto;
import ru.geekbrains.WowVendorTeamHelper.model.WowTeamRegion;
import ru.geekbrains.WowVendorTeamHelper.repository.TeamRepository;
import ru.geekbrains.WowVendorTeamHelper.repository.WowTeamRegionRepository;
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




}
