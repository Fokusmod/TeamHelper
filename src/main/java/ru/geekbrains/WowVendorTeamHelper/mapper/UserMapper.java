package ru.geekbrains.WowVendorTeamHelper.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.geekbrains.WowVendorTeamHelper.dto.UserDto;
import ru.geekbrains.WowVendorTeamHelper.model.User;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserMapper {

    public UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .status(user.getStatus().getTitle())
                .password("")
                .privileges(user.getPrivileges().stream().map(p -> p.getTitle()).collect(Collectors.toList()))
                .build();
    }
}
