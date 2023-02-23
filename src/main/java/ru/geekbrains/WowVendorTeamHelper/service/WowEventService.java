package ru.geekbrains.WowVendorTeamHelper.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.geekbrains.WowVendorTeamHelper.dto.RequestEvents;
import ru.geekbrains.WowVendorTeamHelper.dto.ResponseEvents;
import ru.geekbrains.WowVendorTeamHelper.dto.WowEventDto;
import ru.geekbrains.WowVendorTeamHelper.exeptions.FailedEventListCreationException;
import ru.geekbrains.WowVendorTeamHelper.exeptions.RequestContainsLiteralsException;
import ru.geekbrains.WowVendorTeamHelper.model.Team;
import ru.geekbrains.WowVendorTeamHelper.model.WowEvent;
import ru.geekbrains.WowVendorTeamHelper.model.WowEventType;
import ru.geekbrains.WowVendorTeamHelper.repository.WowEventRepository;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class WowEventService {

    private final DateService dateService;
    private final WowEventRepository wowEventRepository;
    private final WowEventTypeService wowEventTypeService;
    private final TeamService teamService;


    public List<WowEvent> getAllEvents() {
        return wowEventRepository.findAll();
    }

    public List<WowEvent> getByTeamTitle(String string) {
        return wowEventRepository.findByTeamTitle(string);
    }

    public List<WowEvent> getByEventType(String string) {
        return wowEventRepository.findByWowEventTypeTitle(string);
    }

    public List<WowEvent> getByTeamAndType(String team, String type) {
        return wowEventRepository.findByTeamTitleAndWowEventTypeTitle(team, type);
    }

    public WowEventDto changeById(Long id, List<RequestEvents> list) {
        if (isRussianLiterals(list)) {
            throw new FailedEventListCreationException("Запрос на изменение содержит русские литералы.");
        }
        if (!checkDateAndTimeFormat(list)) {
            throw new FailedEventListCreationException("Неудачное создание списка событий.");
        }
        Optional<WowEvent> request = wowEventRepository.findById(id);
        WowEvent wowEvent = null;
        if (request.isPresent()) {
            wowEvent = request.get();
            for (RequestEvents requestEvents : list) {
                Team team = teamService.getTeamByTitle(requestEvents.getTeam());
                WowEventType type = wowEventTypeService.getTypeByTitle(requestEvents.getType());
                wowEvent.setTeam(team);
                wowEvent.setWowEventType(type);
                wowEvent.setEventDate(requestEvents.getDate());
                wowEvent.setStartedAt(requestEvents.getTime());
                wowEventRepository.save(wowEvent);
            }
        }
        return new WowEventDto(wowEvent);
    }

    @Transactional
    public List<ResponseEvents> createEvents(List<RequestEvents> requestEvents) {
        if (isRussianLiterals(requestEvents)) {
            throw new RequestContainsLiteralsException("Запрос на создание содержит русские литералы.");
        }
        if (!checkDateAndTimeFormat(requestEvents)) {
            throw new FailedEventListCreationException("Неудачное создание списка событий.");
        }
        List<RequestEvents> checkedRequestEvents = checkDuplicates(requestEvents);
        List<ResponseEvents> resultList = new ArrayList<>();
        for (RequestEvents request : checkedRequestEvents) {
            WowEvent wowEvent = new WowEvent();
            wowEvent.setEventDate(request.getDate());
            wowEvent.setStartedAt(request.getTime().toUpperCase(Locale.ENGLISH));
            Team team = teamService.getTeamByTitle(request.getTeam());
            wowEvent.setTeam(team);
            WowEventType wowEventType = wowEventTypeService.getTypeByTitle(request.getType());
            wowEvent.setWowEventType(wowEventType);
            wowEventRepository.save(wowEvent);
            resultList.add(new ResponseEvents(
                    wowEvent.getWowEventType().getTitle(),
                    wowEvent.getTeam().getTitle(),
                    wowEvent.getEventDate(),
                    wowEvent.getStartedAt())
            );
        }
        return resultList;
    }

    private boolean isRussianLiterals(List<RequestEvents> requestEvents) {
        boolean result = false;
        for (RequestEvents requestEvent : requestEvents) {
            String[] strings = requestEvent.getTime().split(" ");
            for (String string : strings) {
                if (result) {
                    break;
                }
                try {
                    Integer.parseInt(string);
                } catch (NumberFormatException e) {
                    for (char a : string.toCharArray()) {
                        if (Character.UnicodeBlock.of(a) == Character.UnicodeBlock.CYRILLIC) {
                            result = true;
                            break;
                        }
                    }
                }
            }
        }
        return result;
    }



    //Метод проверки даты и времени
    // Дата - [19:00 МСК / 17:00 CET]
    // Время [18.01.23]
    private boolean checkDateAndTimeFormat(List<RequestEvents> requestEvents) {
        for (RequestEvents event : requestEvents) {
            String date = event.getDate();
            String[] time = event.getTime().split(" ");
            if (!dateService.checkDateFormat(date)) {
                log.debug("Проверка формата даты:" + dateService.checkDateFormat(date));
                return false;
            }
            if (!dateService.checkTimeFormat(time[0])) {
                log.debug("Проверка формата времени:" + dateService.checkTimeFormat(time[0]));
                return false;
            }
            if (!dateService.checkTimeFormat(time[3])) {
                log.debug("Проверка формата времени:" + dateService.checkTimeFormat(time[3]));
                return false;
            }
        }
        return true;
    }

    private List<RequestEvents> checkDuplicates(List<RequestEvents> requestEvents) {
        LinkedHashSet<RequestEvents> linkedHashSet = new LinkedHashSet<>(requestEvents);
        List<RequestEvents> list = new ArrayList<>(linkedHashSet);
        List<WowEvent> wowEventList = wowEventRepository.findAll();
        for (WowEvent wowEvent : wowEventList) {
            for (int i = 0; i < list.size(); i++) {
                if (wowEvent.getTeam().getTitle().equals(list.get(i).getTeam()) &&
                        wowEvent.getWowEventType().getTitle().equals(list.get(i).getType()) &&
                        wowEvent.getEventDate().equals(list.get(i).getDate()) &&
                        wowEvent.getStartedAt().equals(list.get(i).getTime())) {
                    list.remove(list.get(i));
                }
            }
        }
        return list;
    }

    public void deleteEvent(Long id) {
        wowEventRepository.deleteById(id);
    }

}
