package ru.geekbrains.WowVendorTeamHelper.service;

import com.slack.api.bolt.App;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.request.chat.ChatUpdateRequest;
import com.slack.api.methods.request.conversations.ConversationsHistoryRequest;
import com.slack.api.methods.response.conversations.ConversationsHistoryResponse;
import com.slack.api.model.event.MessageChangedEvent;
import com.slack.api.model.event.MessageDeletedEvent;
import com.slack.api.model.event.MessageEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class MessageService {

    //Хранение в базе данных
    //Временное хранение (30 мин)
    //Добавить возможность удаления сообщения
    //Добавить ТУДУ метки
    //Переопределить equals and hashcode
    //Веб-сокетное соединение
    //Нужно возобновление работы после ребута приложения.


    @Lazy
    @Autowired
    private App app;

    private final String testChannel = "C04K0GF2H9Q"; //"test"

    private volatile LinkedHashMap<String, MessageEvent> messageEventList = new LinkedHashMap();


    //Отправка сообщений в канал #test, пока что сразу после получения. После публикации сообщения извлекаеться tts
    //временная переменная (Информация в какой промежутковремени было опублековано сообщение, своего рода идентификатор)
    //и добавляется в мапу
    public void postMessageInChannel(MessageEvent messageEvent) throws IOException, SlackApiException {
        ChatPostMessageRequest chatPostMessageRequest = ChatPostMessageRequest
                .builder()
                .channel(testChannel)
                .text(messageEvent.getText())
                .build();
        app.getClient().chatPostMessage(chatPostMessageRequest);
        System.out.println("postMessageInChannel - send message in " + testChannel);

        ConversationsHistoryRequest conversationsHistoryRequest = ConversationsHistoryRequest
                .builder()
                .channel(testChannel)
                .limit(1)
                .build();

        ConversationsHistoryResponse conversationsHistoryResponse = app.client().conversationsHistory(conversationsHistoryRequest);
        messageEventList.put(conversationsHistoryResponse.getMessages().get(0).getTs(), messageEvent);

    }

    //Получение сообщений из канала со списком клиентов
    public void getMessageMethod(MessageEvent messageEvent) {
        System.out.println("GetMessageMethod - Get new message event");
        try {
            postMessageInChannel(messageEvent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SlackApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void changeMessageMethod(MessageChangedEvent messageChangedEvent) {
        for (Map.Entry<String, MessageEvent> messageEventEntry : messageEventList.entrySet()) {
            if (messageEventEntry.getValue().getTs().equals(messageChangedEvent.getMessage().getTs())) {
                ChatUpdateRequest chatUpdateRequest = ChatUpdateRequest
                        .builder()
                        .channel(testChannel)
                        .text(messageChangedEvent.getMessage().getText())
                        .ts(messageEventEntry.getKey())
                        .build();
                try {
                    app.getClient().chatUpdate(chatUpdateRequest);
                } catch (IOException | SlackApiException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }


    public void deleteMessageMethod(MessageDeletedEvent messageDeletedEvent) {

    }
}
