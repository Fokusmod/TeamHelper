package ru.geekbrains.WowVendorTeamHelper.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.geekbrains.WowVendorTeamHelper.dto.RequestEvents;
import ru.geekbrains.WowVendorTeamHelper.model.Team;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class ParseEventService {
    private final int step = 2;

    public List<RequestEvents> parseRequest(String strings) {
        List<RequestEvents> list = new ArrayList<>();
        String[] rows = strings.replace("\r","").split("\n");
        for (int i = 0; i < rows.length; i++) {
            String[] rowPrepare = rows[i].replace("\t", " ").replaceFirst("-", " ").split(" ");
            RequestEvents requestEvents = new RequestEvents();
            requestEvents.setType(parseType(rowPrepare));
            requestEvents.setTeam(parseTeam(rowPrepare));
            requestEvents.setDate(parseDate(rowPrepare));
            requestEvents.setTime(parseTime(rowPrepare));
            list.add(requestEvents);
        }
        return list;
    }

    public String parseType(String[] arr) {
        StringBuilder type = new StringBuilder();
        for (int i = 0; i < step; i++) {
            if (i == 0) {
                type.append(arr[i]).append(" ");
            } else {
                type.append(arr[i]);
            }
        }
        return type.toString();
    }

    public String parseTeam(String[] arr) {
        StringBuilder team = new StringBuilder();
        for (int i = step; i < step + step; i++) {
            if (i == step) {
                team.append(arr[i]).append("-");
            } else {
                team.append(arr[i]);
            }
        }
        return team.toString().toLowerCase();
    }

    public String parseDate(String[] arr) {
        StringBuilder date = new StringBuilder();
        for (int i = step + step; i < step + step + 1; i++) {
            date.append(arr[i]);
        }
        return date.toString();
    }

    public String parseTime(String[] arr) {
        StringBuilder time = new StringBuilder();
        int dateLength = 5;
        for (int i = arr.length - dateLength; i < arr.length; i++) {
            if (i < arr.length - 1) {
                time.append(arr[i]).append(" ");
            } else {
                time.append(arr[i]);
            }
        }
        return time.toString();
    }
}
