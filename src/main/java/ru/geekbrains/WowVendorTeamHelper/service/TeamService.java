package ru.geekbrains.WowVendorTeamHelper.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.geekbrains.WowVendorTeamHelper.dto.TeamDTO;
import ru.geekbrains.WowVendorTeamHelper.exeptions.WWTHResourceNotFoundException;
import ru.geekbrains.WowVendorTeamHelper.model.Team;
import ru.geekbrains.WowVendorTeamHelper.model.WowTeamRegion;
import ru.geekbrains.WowVendorTeamHelper.repository.TeamRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
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
            log.info("Команда " + title + " не найдена.");
            throw new WWTHResourceNotFoundException("Команда " + title + " не найдена.");
        }
    }

    public List<Team> getTeamsByRegion(String region) {
        WowTeamRegion wowTeamRegion = wowTeamRegionService.getByTitle(region.toUpperCase());
        return repository.findByTeamRegion(wowTeamRegion);
    }


}
