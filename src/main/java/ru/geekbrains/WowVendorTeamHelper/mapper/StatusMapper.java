package ru.geekbrains.WowVendorTeamHelper.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.geekbrains.WowVendorTeamHelper.dto.StatusDto;
import ru.geekbrains.WowVendorTeamHelper.model.Status;

@Component
@RequiredArgsConstructor
public class StatusMapper {

    public StatusDto toDto(Status status) {
        return StatusDto.builder()
                .id(status.getId())
                .title(status.getTitle())
                .build();
    }
}
