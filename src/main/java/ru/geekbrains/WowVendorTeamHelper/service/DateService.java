package ru.geekbrains.WowVendorTeamHelper.service;

import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class DateService {

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("EEE dd MMM @ HH:mm ", Locale.ENGLISH);
    private final DateTimeFormatter scheduleDate = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.getDefault());
    private final DateTimeFormatter formatToDataBase = DateTimeFormatter.ofPattern("dd-MM-yy HH:mm", Locale.ENGLISH);
    private final ZoneId mskZone = ZoneId.of("Europe/Moscow");
    private final ZoneId cetZone = ZoneId.of("CET");

    public List<String> getTimeMscAndCET() {
//      формат  (Sat 28 Jan @ 21:00 CET)
        List<String> timeList = new ArrayList<>();
        String mskTime = LocalDateTime.now(mskZone).format(dateTimeFormatter) + "MSC";
        String cetTime = LocalDateTime.now(cetZone).format(dateTimeFormatter) + "CET";
        timeList.add(mskTime);
        timeList.add(cetTime);
        return timeList;
    }

    public List<String> getCurrentWeek() {
        String currentDate;
        List<String> currentDayWeek = new ArrayList<>();
        LocalDate previousMonday = LocalDate.now(mskZone).with(TemporalAdjusters.previous(DayOfWeek.MONDAY));
        for (int i = 0; i < 7; i++) {
            currentDate = previousMonday.plusDays(i).format(scheduleDate);
            currentDayWeek.add(currentDate);


            String date = "2017-03-08 12:30";
            DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime dateTime = LocalDateTime.parse(date, format);
            System.out.println(dateTime);

        }
        return currentDayWeek;
    }

    public List<String> getNextWeek(int count) {
        String weekDay;
        List<String> nextDayWeek = new ArrayList<>();
        LocalDate previousMonday = LocalDate.now(mskZone).with(TemporalAdjusters.previous(DayOfWeek.MONDAY));
        LocalDate mondayOnNextWeek = previousMonday.plusWeeks(count).with(DayOfWeek.MONDAY);
        for (int i = 0; i < 7; i++) {
            weekDay = mondayOnNextWeek.plusDays(i).format(scheduleDate);
            nextDayWeek.add(weekDay);
        }
        return nextDayWeek;
    }

    public List<String> getPrevWeek(int count) {
        String weekDay;
        List<String> prevDayWeek = new ArrayList<>();
        LocalDate previousMonday = LocalDate.now(mskZone).with(TemporalAdjusters.previous(DayOfWeek.MONDAY));
        LocalDate mondayOnPreviousWeek = previousMonday.minusWeeks(count).with(DayOfWeek.MONDAY);
        for (int i = 0; i < 7; i++) {
            weekDay = mondayOnPreviousWeek.plusDays(i).format(scheduleDate);
            prevDayWeek.add(weekDay);
        }
        return prevDayWeek;
    }

}
