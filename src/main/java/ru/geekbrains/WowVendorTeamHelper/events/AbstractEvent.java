package ru.geekbrains.WowVendorTeamHelper.events;

import org.springframework.context.ApplicationEvent;

public class AbstractEvent extends ApplicationEvent {

    public AbstractEvent() {
        super(new Object());
    }

    public AbstractEvent(Object source) {
        super(source);
    }
}
