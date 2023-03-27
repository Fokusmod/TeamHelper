package ru.geekbrains.WowVendorTeamHelper.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.geekbrains.WowVendorTeamHelper.service.DateService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/date")
public class DateTimeController {

    private final DateService dateService;

    @GetMapping("/current/time")
    public List<String> getCurrentTime () {
        return dateService.getTimeMscAndCET();
    }

    @GetMapping("/current/date-week")
    public List<String> getCurrentWeek () {
        return dateService.getCurrentWeek();
    }

    @GetMapping("/next/date-week/{count}")
    public List<String> getNextWeek (@PathVariable int count) {
        return dateService.getNextWeek(count);
    }

    @GetMapping("/prev/date-week/{count}")
    public List<String> getPrevWeek (@PathVariable int count) {
        return dateService.getPrevWeek(count);
    }


}
