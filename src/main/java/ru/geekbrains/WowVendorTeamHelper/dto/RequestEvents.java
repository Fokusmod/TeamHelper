package ru.geekbrains.WowVendorTeamHelper.dto;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RequestEvents {

    private String type;

    private String team;

    private String date;

    private String time;
}
