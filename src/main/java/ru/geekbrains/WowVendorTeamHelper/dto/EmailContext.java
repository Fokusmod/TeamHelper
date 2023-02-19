package ru.geekbrains.WowVendorTeamHelper.dto;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailContext {

    private String from;
    private String to;
    private String subject;
    private String email;
    private String template;
    private Map<String, Object> properties;
}
