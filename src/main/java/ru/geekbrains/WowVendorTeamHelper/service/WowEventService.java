package ru.geekbrains.WowVendorTeamHelper.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.geekbrains.WowVendorTeamHelper.dto.RequestEvents;
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

    public void changeById(Long id, List<RequestEvents> list) {
        if (isRussianLiterals(list)) {
            log.error("changeRequest contains Russian literals.");
            throw new RuntimeException("changeRequest contains Russian literals.");
        }
        if (checkDateFormat(list)) {
            Optional<WowEvent> request = wowEventRepository.findById(id);
            if (request.isPresent()) {
                WowEvent wowEvent = request.get();
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
        } else {
            log.error("Неудачное создание списка событий.");
            throw new RuntimeException("Неудачное создание списка событий.");
        }


    }

    @Transactional
    public void createEvents(List<RequestEvents> requestEvents) {
        if (isRussianLiterals(requestEvents)) {
            log.error("createRequest contains Russian literals.");
            throw new RuntimeException("createRequest contains Russian literals.");
        }
        if (checkDateFormat(requestEvents)) {
            List<RequestEvents> checkedRequestEvents = checkDoubles(requestEvents);
            for (RequestEvents request : checkedRequestEvents) {
                WowEvent wowEvent = new WowEvent();
                wowEvent.setEventDate(request.getDate());
                wowEvent.setStartedAt(request.getTime().toUpperCase(Locale.ENGLISH));
                Team team = teamService.getTeamByTitle(request.getTeam());
                wowEvent.setTeam(team);
                WowEventType wowEventType = wowEventTypeService.getTypeByTitle(request.getType());
                wowEvent.setWowEventType(wowEventType);
                wowEventRepository.save(wowEvent);
            }
        } else {
            log.error("Неудачное создание списка событий.");
            throw new RuntimeException("Неудачное создание списка событий.");
        }
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
    private boolean checkDateFormat(List<RequestEvents> requestEvents) {
        for (RequestEvents event : requestEvents) {
            String date = event.getDate();
            String[] time = event.getTime().split(" ");
            if (!dateService.checkDateFormat(date)) {
                log.info("Проверка формата даты:" + dateService.checkDateFormat(date));
                return false;
            }
            if (!dateService.checkTimeFormat(time[0])) {
                log.info("Проверка формата времени:" + dateService.checkTimeFormat(time[0]));
                return false;
            }
            if (!dateService.checkTimeFormat(time[3])) {
                log.info("Проверка формата времени:" + dateService.checkTimeFormat(time[3]));
                return false;
            }
        }
        return true;
    }

    private List<RequestEvents> checkDoubles(List<RequestEvents> requestEvents) {
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
