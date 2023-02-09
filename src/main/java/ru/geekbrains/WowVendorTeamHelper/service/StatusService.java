package ru.geekbrains.WowVendorTeamHelper.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.geekbrains.WowVendorTeamHelper.exeptions.ResourceNotFoundException;
import ru.geekbrains.WowVendorTeamHelper.model.Status;
import ru.geekbrains.WowVendorTeamHelper.repository.StatusRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StatusService {
    private final StatusRepository statusRepository;

    public Status saveOrUpdateStatus(Status status) {
        return statusRepository.save(status);
    }

    public boolean deleteStatus(Long id) {
        statusRepository.deleteById(id);
        return true;
    }

    public Status findById(Long statusId) {
        return statusRepository.findById(statusId).orElseThrow(() ->
                new ResourceNotFoundException("Не найдено статуса с id: " + statusId));
    }

    public List<Status> getAllStatus() {
        return statusRepository.findAll();
    }

    public Status findByTitle(String title) {
        return statusRepository.findByTitle(title).orElseThrow(() ->
                new ResourceNotFoundException("Не найдено статуса с id: " + title));
    }

    public Status getStatus(Long id, String title) {
        if (id != null) {
            return findById(id);
        }
        if (title != null) {
            return findByTitle(title);
        }
        return null;
    }
}