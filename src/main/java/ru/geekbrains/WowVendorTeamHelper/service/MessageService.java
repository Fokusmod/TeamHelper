package ru.geekbrains.WowVendorTeamHelper.service;

import com.slack.api.bolt.App;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatDeleteRequest;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.request.chat.ChatUpdateRequest;
import com.slack.api.methods.request.conversations.ConversationsHistoryRequest;
import com.slack.api.methods.response.conversations.ConversationsHistoryResponse;
import com.slack.api.model.event.MessageChangedEvent;
import com.slack.api.model.event.MessageDeletedEvent;
import com.slack.api.model.event.MessageEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.geekbrains.WowVendorTeamHelper.model.MyMessage;
import ru.geekbrains.WowVendorTeamHelper.model.TeamChannelId;
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

    //Отправляет сообщение в канал (тестовый) и заносит данные в бд
    public void postMessageInChannel(MessageEvent messageEvent) throws IOException, SlackApiException {
        String testChannel = TeamChannelId.TEST.getValue();
        ChatPostMessageRequest chatPostMessageRequest = ChatPostMessageRequest
                .builder()
                .channel(testChannel)
                .text(messageEvent.getText())
                .build();
        app.getClient().chatPostMessage(chatPostMessageRequest);
        log.info("опубликовано сообщение в канале - сообщение отправилено в "+ testChannel);
        ConversationsHistoryRequest conversationsHistoryRequest = ConversationsHistoryRequest
                .builder()
                .channel(testChannel)
                .limit(1)
                .build();
        ConversationsHistoryResponse conversationsHistoryResponse = app.client().conversationsHistory(conversationsHistoryRequest);
        Optional<MyMessage> message = repository.findByTs(messageEvent.getTs());
        if (message.isPresent()) {
            MyMessage myMessage = message.get();
            myMessage.setChannelMessageTs(conversationsHistoryResponse.getMessages().get(0).getTs());
            repository.save(myMessage);
        }
    }

    //Получение сообщений из канала со списком клиентов
    public void getMessageMethod(MessageEvent messageEvent) {
        log.info("Метод GetMessage - Получение нового события сообщения.");
        MyMessage message = new MyMessage();
        message.setText(messageEvent.getText());
        message.setTs(messageEvent.getTs());
        message.setChannel(messageEvent.getChannel());
        repository.save(message);
        try {
            postMessageInChannel(messageEvent);
        } catch (IOException | SlackApiException e) {
            log.error("Ошибка при отправке сообщения.", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    //Изменяет сообщение в канале в котором находится это сообщение
    public void changeMessageMethod(MessageChangedEvent messageChangedEvent) {
        String testChannel = TeamChannelId.TEST.getValue();
        Optional<MyMessage> message = repository.findByTs(messageChangedEvent.getMessage().getTs());
        if (message.isPresent()) {
            MyMessage myMessage = message.get();
            ChatUpdateRequest chatUpdateRequest = ChatUpdateRequest
                    .builder()
                    .channel(testChannel)
                    .text(messageChangedEvent.getMessage().getText())
                    .ts(myMessage.getChannelMessageTs())
                    .build();
            try {
                app.getClient().chatUpdate(chatUpdateRequest);
                myMessage.setText(messageChangedEvent.getMessage().getText());
                repository.save(myMessage);
            } catch (IOException | SlackApiException e) {
                log.error("Ошибка при изменении сообщения в канале.", e.getMessage());
                throw new RuntimeException(e);
            }
        }

    }

    //Удаляет сообщение из бд и из канал в котором сообщение опубликовано
    public void deleteMessageMethod(MessageDeletedEvent messageDeletedEvent) {
        String testChannel = TeamChannelId.TEST.getValue();
        Optional<MyMessage> message = repository.findByTs(messageDeletedEvent.getDeletedTs());
        if (message.isPresent()) {
            MyMessage myMessage = message.get();
            ChatDeleteRequest chatDeleteRequest = ChatDeleteRequest
                    .builder()
                    .channel(testChannel)
                    .ts(myMessage.getChannelMessageTs())
                    .build();
            try {
                app.getClient().chatDelete(chatDeleteRequest);
                repository.delete(myMessage);
            } catch (IOException | SlackApiException e) {
                log.error("Ошибка при удалении сообщения.", e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }

    public List<MyMessage> getAllMessages() {
        List<MyMessage> messageList = new ArrayList<>();
        repository.findAll().forEach(messageList::add);
        return messageList;
    }

    public void deleteAllMessage() {
        repository.deleteAll();
    }
}
