package ru.geekbrains.WowVendorTeamHelper.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.geekbrains.WowVendorTeamHelper.dto.StatusDto;
import ru.geekbrains.WowVendorTeamHelper.exeptions.WWTHResourceNotFoundException;
import ru.geekbrains.WowVendorTeamHelper.model.Status;
import ru.geekbrains.WowVendorTeamHelper.repository.StatusRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StatusService {
    private final StatusRepository statusRepository;

    public Status saveStatus(StatusDto statusDto) {
        Status status = new Status();
        status.setTitle(statusDto.getTitle());
        return statusRepository.save(status);
    }

    public boolean deleteStatus(Long id) {
        statusRepository.deleteById(id);
        return true;
    }

    public Status findById(Long statusId) {
        return statusRepository.findById(statusId).orElseThrow(() ->
                new WWTHResourceNotFoundException("Статус с идентификатором '" + statusId + "' не найден."));
    }

    public List<Status> getAllStatus() {
        return statusRepository.findAll();
    }

    public Status findByTitle(String title) {
        return statusRepository.findByTitle(title).orElseThrow(() ->
                new WWTHResourceNotFoundException("Статус '" + title + "' не найден."));
    }

}
