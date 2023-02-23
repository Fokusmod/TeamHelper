package ru.geekbrains.WowVendorTeamHelper.dto;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResponseEvents {
    private String type;

    private String team;

    private String date;

    private String time;

    public ResponseEvents(String type, String team, String date, String time) {
        this.type = type;
        this.team = team;
        this.date = date;
        this.time = time;
    }
}
