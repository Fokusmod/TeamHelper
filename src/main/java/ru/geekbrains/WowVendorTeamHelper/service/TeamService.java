package ru.geekbrains.WowVendorTeamHelper.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.geekbrains.WowVendorTeamHelper.dto.TeamDTO;
import ru.geekbrains.WowVendorTeamHelper.exeptions.TeamNotFoundException;
import ru.geekbrains.WowVendorTeamHelper.model.Team;
import ru.geekbrains.WowVendorTeamHelper.repository.TeamRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository repository;

    private final WowTeamRegionService wowTeamRegionService;

    public List<Team> getAllTeams() {
        return repository.findAll();
    }

    public void addTeam(TeamDTO teamDTO) {
        Team team = new Team();
        team.setTitle(teamDTO.getTitle());
        team.setTeamRegion(teamDTO.getRegion());

    }

    public Team getTeamByTitle(String title) {
        Optional<Team> team = repository.getByTitle(title);
        if (team.isPresent()){
            return team.get();
        } else {
            throw new TeamNotFoundException("Team " + title + " not found");
        }
    }


    public void deleteTeam() {

    }

    public void changeTeam() {

    }

    public void getTeamsByRegion() {

    }


}
