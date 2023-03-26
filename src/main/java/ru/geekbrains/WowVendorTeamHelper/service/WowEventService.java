package ru.geekbrains.WowVendorTeamHelper.service;

import liquibase.pro.packaged.S;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.geekbrains.WowVendorTeamHelper.dto.*;
import ru.geekbrains.WowVendorTeamHelper.exeptions.WWTHBadRequestException;
import ru.geekbrains.WowVendorTeamHelper.exeptions.WWTHResourceNotFoundException;
import ru.geekbrains.WowVendorTeamHelper.model.*;
import ru.geekbrains.WowVendorTeamHelper.repository.WowEventRepository;
import ru.geekbrains.WowVendorTeamHelper.repository.WowEventStatusRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class WowEventService {

    private final DateService dateService;
    private final WowEventRepository wowEventRepository;
    private final WowEventTypeService wowEventTypeService;
    private final WowEventStatusRepository wowEventStatusRepository;
    private final OrderStatusService statusService;
    private final WowClientService wowClientService;

    private final BundleService bundleService;
    private final TeamService teamService;
    @PersistenceContext
    private final EntityManager entityManager;

    private final String CREATED_STATUS = "CREATED";

    private final String STATUS_DEFINED = "DEFINED";

    private final String CHANGE_EVENT = "EVENT";

    private final Object MONITOR = new Object();


    public List<WowEvent> getAllEvents() {
        return wowEventRepository.findAll();
    }

    public WowEvent getById(Long id) {
        return wowEventRepository.getById(id);
    }

    public List<WowEvent> getEventsByArrayTeam(List<TeamDTO> teams) {
        List<WowEvent> eventList = new ArrayList<>();
        for (TeamDTO team : teams) {
            List<WowEvent> teamEventList = getByTeamTitle(team.getTitle());
            eventList.addAll(teamEventList);
        }
        return eventList;
    }


    public void addClientInEvent(Long eventId, Long clientId) {
        clientInEvent(eventId, clientId);
        wowClientService.sendMessageToTopic(CHANGE_EVENT + "/" + eventId + "/" + clientId);
        wowClientService.sendMessageToTopic(WowClientService.EVENT_CHANGE_CLIENTS + "/" + clientId);
    }

    @Transactional
    public void clientInEvent(Long eventId, Long clientId) {
        WowEvent event = getById(eventId);
        WowClient client = wowClientService.getClientById(clientId);
        OrderStatus status = statusService.getOrderStatusByTitle(STATUS_DEFINED);
        if (client.getBundle().equals("true")|| client.getOrderCount() != null) {
            checkDefinedAllBundleStage(event, client);
            return;
        }
        client.setOrderStatus(status);
        event.getClientList().add(client);
        wowEventRepository.saveAndFlush(event);
    }

    private void checkDefinedAllBundleStage(WowEvent wowEvent, WowClient wowClient) {
        OrderStatus defined = statusService.getOrderStatusByTitle(STATUS_DEFINED);
        BundleStage bundleStage = convertToBundleStage(wowEvent.getWowEventType());
        List<ClientsBundleStages> stages = wowClient.getClientsBundleStages();

        boolean findChangeStage = false;

        if (wowClient.getOrderCount() != null) {
            for (ClientsBundleStages stage : stages) {
                if (stage.getBundleStage().getTitle().equals(bundleStage.getTitle())) {
                    if (stage.getOrderStatus().getTitle().equals(STATUS_DEFINED)) {
                        continue;
                    }
                    stage.setOrderStatus(defined);
                    findChangeStage = true;
                    break;
                }
            }
        } else {
            for (ClientsBundleStages stage : stages) {
                if (stage.getBundleStage().getTitle().equals(bundleStage.getTitle())) {
                    System.out.println(stage.getBundleStage().getTitle() + " " + stage.getOrderStatus().getTitle());
                    if (stage.getOrderStatus().getTitle().equals(STATUS_DEFINED)) {
                        throw new WWTHBadRequestException("Этап заказа " + stage.getBundleStage().getTitle() + " уже определен.");
                    }
                    stage.setOrderStatus(defined);
                    findChangeStage = true;
                    break;
                }
            }
        }

        if (!findChangeStage) throw new WWTHBadRequestException("В заказе отсутствует корректный этап бандла. " +
                "Проверьте назначеные этапы бандла на соответствие правилам.");

        wowClient.setClientsBundleStages(stages);

        boolean definedAll = true;
        for (ClientsBundleStages stage : stages) {
            if (!stage.getOrderStatus().getTitle().equals(STATUS_DEFINED)) {
                definedAll = false;
            }
        }
        if (definedAll) {
            wowClient.setOrderStatus(defined);
        }
        wowClientService.saveClient(wowClient);
        wowEvent.getClientList().add(wowClient);
        wowEventRepository.saveAndFlush(wowEvent);
    }

    private BundleStage convertToBundleStage(WowEventType wowEventType) {
        String[] correctType = wowEventType.getTitle().toUpperCase().split(" ");
        String raid = null;
        for (int i = 0; i < correctType.length; i++) {
            if (correctType[i].contains("HC")) {
                raid = "HC";
            } else if (correctType[i].contains("NM")) {
                raid = "NM";
            } else if (correctType[i].contains("MYTH")) {
                raid = "MYTH";
            }
        }

        String correctBundleStage = null;
        if (raid != null) {
            switch (raid) {
                case "HC":
                    correctBundleStage = "Heroic Raid";
                    break;
                case "NM":
                    correctBundleStage = "Normal Raid";
                    break;
                case "MYTH":
                    correctBundleStage = "Mythic Raid";
                    break;
                default:
                    throw new WWTHResourceNotFoundException("Событие определено некорректно, заказ не содержит " +
                            "подходящего этапа для бандла");
            }
        }
        if (correctBundleStage != null) {
            return bundleService.getBundleStageByTitle(correctBundleStage);
        }
        throw new WWTHResourceNotFoundException("Заказ невозможно определить в конкретный рейд. Этап бандла не " +
                "удалось определить.");

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

    public List<WowEvent> getByStatus(String status) {
        return wowEventRepository.findByWowEventStatusTitle(status);
    }

    public void saveEvent(WowEvent wowEvent) {
        wowEventRepository.saveAndFlush(wowEvent);
    }

    public List<WowEvent> getActiveEvents(String message) {
        String[] ids = message.split(" ");
        List<WowEvent> wowEventList = new ArrayList<>();
        for (int i = 0; i < ids.length; i++) {
            wowEventList.add(wowEventRepository.getById(Long.valueOf(ids[i])));
        }
        return wowEventList;
    }

    public List<WowEvent> getStartActiveEvents() {
        return wowEventRepository.findByWowEventStatusTitle("active");
    }

    public WowEventDto changeById(Long id, List<RequestEvents> list) {
        if (isRussianLiterals(list)) {
            throw new WWTHBadRequestException("В списке событий содержатся русские символы.");
        }
        if (checkFalseDateAndTimeFormat(list)) {
            throw new WWTHBadRequestException("Неправильный формат даты или времени.");
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
        } else {
            throw new WWTHResourceNotFoundException("Изменяемого вами события больше не существует");
        }
        return new WowEventDto(wowEvent);
    }

    @Transactional
    public List<ResponseEvents> createEvents(List<RequestEvents> requestEvents) {
        if (isRussianLiterals(requestEvents)) {
            throw new WWTHBadRequestException("В списке событий содержатся русские символы.");
        }
        if (checkFalseDateAndTimeFormat(requestEvents)) {
            throw new WWTHBadRequestException("Неправильный формат даты или времени.");
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
            Optional<WowEventStatus> created = wowEventStatusRepository.findByTitle(CREATED_STATUS.toLowerCase());
            if (created.isPresent()) {
                wowEvent.setWowEventStatus(created.get());
            } else {
                throw new WWTHResourceNotFoundException("Wow_event_status 'created' не найден.");
            }
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

    private boolean checkFalseDateAndTimeFormat(List<RequestEvents> requestEvents) {
        for (RequestEvents event : requestEvents) {
            String date = event.getDate();
            String[] time = event.getTime().split(" ");
            if (!dateService.checkDateFormat(date)) {
                log.debug("Проверка формата даты:" + dateService.checkDateFormat(date));
                return true;
            }
            if (!dateService.checkTimeFormat(time[0])) {
                log.debug("Проверка формата времени:" + dateService.checkTimeFormat(time[0]));
                return true;
            }
            if (!dateService.checkTimeFormat(time[3])) {
                log.debug("Проверка формата времени:" + dateService.checkTimeFormat(time[3]));
                return true;
            }
        }
        return false;
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
