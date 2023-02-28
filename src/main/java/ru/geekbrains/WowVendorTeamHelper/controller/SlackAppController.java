package ru.geekbrains.WowVendorTeamHelper.controller;


import com.slack.api.bolt.App;
import com.slack.api.bolt.servlet.SlackAppServlet;
import com.slack.api.model.event.MessageChangedEvent;
import com.slack.api.model.event.MessageDeletedEvent;
import com.slack.api.model.event.MessageEvent;
import org.springframework.beans.factory.annotation.Autowired;
import ru.geekbrains.WowVendorTeamHelper.service.MessageService;

import javax.servlet.annotation.WebServlet;


@WebServlet("/slack/events")
public class SlackAppController extends SlackAppServlet {

    private final MessageService messageService;

    public SlackAppController(App app, @Autowired MessageService messageService) {
        super(app);
        this.messageService = messageService;

        app.event(MessageEvent.class, (payload, ctx) -> {
            if (payload.getEvent().getChannel().equals("C04K26N5R70")) {
                app.executorService().submit(() -> {
                    MessageEvent messageEvent = payload.getEvent();
                    if (messageEvent.getText().contains("===")) {
                        this.messageService.getMessageMethod(messageEvent);
                    }
                });
            }
            return ctx.ack();
        });

        app.event(MessageChangedEvent.class, (payload, ctx) -> {
            app.executorService().submit(()->{
                MessageChangedEvent messageChangedEvent = payload.getEvent();
                this.messageService.changeMessageMethod(messageChangedEvent);
            });
            return ctx.ack();
        });

        app.event(MessageDeletedEvent.class, (payload, ctx) -> {
            app.executorService().submit(()->{
                MessageDeletedEvent messageDeletedEvent = payload.getEvent();
                this.messageService.deleteMessageMethod(messageDeletedEvent);
            });
            return ctx.ack();
        });
    }
}
