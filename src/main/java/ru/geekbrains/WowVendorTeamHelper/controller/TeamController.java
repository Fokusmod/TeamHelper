package ru.geekbrains.WowVendorTeamHelper.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.geekbrains.WowVendorTeamHelper.dto.TeamDTO;
import ru.geekbrains.WowVendorTeamHelper.repository.TeamRepository;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class TeamController {

    private final TeamRepository teamRepository;



    @GetMapping("/teams")
    public List<TeamDTO> getTeams (){
        return teamRepository.findAll().stream().map(TeamDTO::new).collect(Collectors.toList());
    }

}
