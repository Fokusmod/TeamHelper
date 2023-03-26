package ru.geekbrains.WowVendorTeamHelper.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import ru.geekbrains.WowVendorTeamHelper.exeptions.WWTHResourceNotFoundException;
import ru.geekbrains.WowVendorTeamHelper.model.WowEvent;
import ru.geekbrains.WowVendorTeamHelper.model.WowEventStatus;
import ru.geekbrains.WowVendorTeamHelper.repository.WowEventStatusRepository;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.swing.text.html.Option;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventsTaskService {

    private final WowEventService wowEventService;
    private final SimpMessagingTemplate messagingTemplate;
    private final WowEventStatusRepository wowEventStatusRepository;
    private final TaskScheduler taskScheduler;
    private ScheduledFuture<?> scheduledFuture;
    private final String ACTIVE_STATUS = "ACTIVE";
    private final ZoneId moscowZone = ZoneId.of("Europe/Moscow");
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yy");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");


    @PostConstruct
    public void startNotificationScheduler() {
        scheduledFuture = taskScheduler.scheduleAtFixedRate(this::checkEventsAndNotify, Duration.ofMinutes(1));
    }

    private void sendMessageToTopic(String message) {
        messagingTemplate.convertAndSend("/events/message", message);
    }

    private void checkEventsAndNotify() {
        StringBuilder message = new StringBuilder();
        WowEventStatus active = getEventsStatusByTitle(ACTIVE_STATUS.toLowerCase());
        setActiveStatusEventsOneHourBeforeStart();
        List<WowEvent> wowEventList = wowEventService.getByStatus(active.getTitle());

        if (wowEventList.size()!=0) {
            message.append(ACTIVE_STATUS).append("/");
            for (WowEvent wowEvent : wowEventList) {
                message.append(wowEvent.getId()).append(" ");
            }
            message.trimToSize();
            sendMessageToTopic(message.toString());
        }
    }

    private WowEventStatus getEventsStatusByTitle(String title) {
        return wowEventStatusRepository.findByTitle(title)
                .orElseThrow(() -> new WWTHResourceNotFoundException("Статус active не найден."));
    }

    private void setActiveStatusEventsOneHourBeforeStart() {
        WowEventStatus active = getEventsStatusByTitle(ACTIVE_STATUS.toLowerCase());
        LocalDateTime now = LocalDateTime.now(moscowZone);
        List<WowEvent> wowEventList = wowEventService.getAllEvents();

        for (WowEvent wowEvent : wowEventList) {
            String[] timeStrings = wowEvent.getStartedAt().split(" ");
            LocalTime time = LocalTime.parse(timeStrings[0], timeFormatter);
            LocalDate date = LocalDate.parse(wowEvent.getEventDate(), dateFormatter);
            LocalDateTime eventsLocalTime = LocalDateTime.of(date, time);
            if (eventsLocalTime.minusHours(1).isBefore(now) && eventsLocalTime.isAfter(now)) {
                if (!wowEvent.getWowEventStatus().getTitle().equals(ACTIVE_STATUS.toLowerCase())) {
                    wowEvent.setWowEventStatus(active);
                    wowEventService.saveEvent(wowEvent);
                }
            }
        }
    }

    @PreDestroy
    public void stopNotificationScheduler() {
        scheduledFuture.cancel(true);
    }
}
