package ru.geekbrains.WowVendorTeamHelper.service;

import com.slack.api.bolt.App;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.request.conversations.ConversationsHistoryRequest;
import com.slack.api.methods.response.conversations.ConversationsHistoryResponse;
import com.slack.api.model.event.MessageChangedEvent;
import com.slack.api.model.event.MessageDeletedEvent;
import com.slack.api.model.event.MessageEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.geekbrains.WowVendorTeamHelper.model.SlackMessageInfo;
import ru.geekbrains.WowVendorTeamHelper.utils.emuns.TeamChannelId;
import ru.geekbrains.WowVendorTeamHelper.repository.SlackMessageRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {

    //Нужна проверка что сообщение доставлено из определенного а не из тестового. TODO

    //Нужно отследить изменения названия каналов. Затем изменить их названия в бд //TODO

    private final App app;
    private final SlackMessageRepository repository;
    private final WowClientService wowClientService;

    public void postMessageInChannel(MessageEvent messageEvent) throws IOException, SlackApiException {
        String testChannel = TeamChannelId.TEST.getValue();
        ChatPostMessageRequest chatPostMessageRequest = ChatPostMessageRequest
                .builder()
                .channel(testChannel)
                .text(messageEvent.getText())
                .build();
        app.getClient().chatPostMessage(chatPostMessageRequest);
        log.info("опубликовано сообщение в канале - сообщение отправилено в " + testChannel);
        ConversationsHistoryRequest conversationsHistoryRequest = ConversationsHistoryRequest
                .builder()
                .channel(testChannel)
                .limit(1)
                .build();
        ConversationsHistoryResponse conversationsHistoryResponse = app.client().conversationsHistory(conversationsHistoryRequest);
        Optional<SlackMessageInfo> message = repository.findByTs(messageEvent.getTs());
        if (message.isPresent()) {
            SlackMessageInfo slackMessageInfo = message.get();
            slackMessageInfo.setOtherChannelMessageTs(conversationsHistoryResponse.getMessages().get(0).getTs());
            repository.save(slackMessageInfo);
        }
    }


    public void getMessageMethod(MessageEvent messageEvent) {
        log.info("Получено новое сообщение из Slack канала.");
        SlackMessageInfo message = new SlackMessageInfo();
        message.setText(messageEvent.getText());
        message.setTs(messageEvent.getTs());
        message.setChannel(messageEvent.getChannel());
        repository.save(message);
        wowClientService.saveParseClients(message);
    }

    public void changeMessageMethod(MessageChangedEvent messageChangedEvent) {
        log.info("Полученое Slack событие на изменение сообщения.");
        Optional<SlackMessageInfo> message = repository.findByTs(messageChangedEvent.getMessage().getTs());
        if (message.isPresent()) {
            SlackMessageInfo slackMessageInfo = message.get();
            slackMessageInfo.setText(messageChangedEvent.getMessage().getText());
            repository.save(slackMessageInfo);
            wowClientService.changeWowClientFromSlack(slackMessageInfo);
        }
    }

    public void deleteMessageMethod(MessageDeletedEvent messageDeletedEvent) {
        log.info("Полученое Slack событие на удаление сообщения.");
        Optional<SlackMessageInfo> message = repository.findByTs(messageDeletedEvent.getDeletedTs());
        if (message.isPresent()) {
            SlackMessageInfo slackMessageInfo = message.get();
            repository.delete(slackMessageInfo);
            wowClientService.deleteWowClientFromSlack(slackMessageInfo);
        }
    }

    public List<SlackMessageInfo> getAllMessages() {
        List<SlackMessageInfo> messageList = new ArrayList<>();
        repository.findAll().forEach(messageList::add);
        return messageList;
    }

    public void deleteAllMessage() {
        repository.deleteAll();
    }
}
