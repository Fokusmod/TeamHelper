package ru.geekbrains.WowVendorTeamHelper.service;

import com.slack.api.bolt.App;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.conversations.ConversationsListRequest;
import com.slack.api.methods.response.conversations.ConversationsListResponse;
import com.slack.api.model.Conversation;
import com.slack.api.model.ConversationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.geekbrains.WowVendorTeamHelper.dto.SlackChannelRequest;
import ru.geekbrains.WowVendorTeamHelper.dto.SlackChannelResponse;
import ru.geekbrains.WowVendorTeamHelper.exeptions.WWTHErrorException;
import ru.geekbrains.WowVendorTeamHelper.exeptions.WWTHResourceNotFoundException;
import ru.geekbrains.WowVendorTeamHelper.model.SlackChannel;
import ru.geekbrains.WowVendorTeamHelper.model.SlackChannelDestination;
import ru.geekbrains.WowVendorTeamHelper.repository.SlackChannelDestinationRepository;
import ru.geekbrains.WowVendorTeamHelper.repository.SlackChannelRepository;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class SlackChannelService {

    private final App app;
    private final SlackChannelRepository slackChannelRepository;
    private final SlackChannelDestinationRepository slackChannelDestinationRepository;


    public List<SlackChannel> getAllSlackChannels() {
        return slackChannelRepository.findAll();
    }
    public ResponseEntity<?> addingNewChannel(SlackChannelRequest request) {
        ResponseEntity<?> responseEntity = findSlackChannel(request);
        if (responseEntity.getStatusCode().value() == HttpStatus.OK.value()) {
            SlackChannelResponse response = (SlackChannelResponse) responseEntity.getBody();
            assert response != null;
            return createChannel(response);
        } else {
            return responseEntity;
        }
    }

    private ResponseEntity<?> findSlackChannel(SlackChannelRequest request) {
        ConversationsListRequest conversationsListRequest = ConversationsListRequest
                .builder()
                .types(List.of(ConversationType.PUBLIC_CHANNEL, ConversationType.PRIVATE_CHANNEL))
                .build();
        try {
            ConversationsListResponse conversationsListResponse = app.getClient().conversationsList(conversationsListRequest);
            List<Conversation> channels = conversationsListResponse.getChannels();
            for (Conversation channel : channels) {
                if (channel.getId().equals(request.getChannelId())) {
                    log.debug("workspace contains a channel [" + request.getChannelId() + "] from sent request");
                    SlackChannelResponse slackChannelResponse = new SlackChannelResponse();
                    slackChannelResponse.setChannelId(channel.getId());
                    slackChannelResponse.setTitle(channel.getName());
                    slackChannelResponse.setDestination(request.getDestination());
                    return ResponseEntity.ok(slackChannelResponse);
                }
            }
            log.debug("workspace does not contain channel with id: [" + request.getChannelId() + "]");
            return new ResponseEntity<>("В вашем рабочем пространстве нет канала с таким ID: " + request.getChannelId(),
                    HttpStatus.BAD_REQUEST);
        } catch (IOException | SlackApiException e) {
            log.error(e.getMessage());
            throw new WWTHErrorException("Произошла ошибка на стороне сервера, связанная со Slack API.");
        }
    }

    public ResponseEntity<?> createChannel(SlackChannelResponse response) {
        Optional<SlackChannel> slackChannel = slackChannelRepository.findByChannelId(response.getChannelId());
        if (slackChannel.isEmpty()) {
            SlackChannel channel = new SlackChannel();
            Optional<SlackChannelDestination> destination = slackChannelDestinationRepository.findByTitle(response.getDestination());
            if (destination.isPresent()) {
                channel.setSlackChannelDestination(Set.of(destination.get()));
                channel.setChannelId(response.getChannelId());
                channel.setTitle(response.getTitle());
                slackChannelRepository.save(channel);
                log.debug("Successful adding channel [" + response.getTitle() + "] with parameter " + response.getDestination() + " in database");
                return new ResponseEntity<>("Канал " + response.getTitle() + " c параметром " + response.getDestination() + " успешно был создан.", HttpStatus.OK);
            } else {
                log.debug("Destinations " + response.getDestination() + " not found. Adding SlackChannel failed with id: " + response.getChannelId());
                throw new WWTHResourceNotFoundException("Параметра " + response.getDestination() + " не существует. Проверьте правильность данных");
            }
        } else {
            SlackChannel channel = slackChannel.get();
            Set<SlackChannelDestination> slackChannelDestination = channel.getSlackChannelDestination();
            for (SlackChannelDestination destination : slackChannelDestination) {
                if (destination.getTitle().equals(response.getDestination())) {
                    log.debug("Channel [" + response.getTitle() + "] with parameter " + response.getDestination() + " already exists in database");
                    return new ResponseEntity<>("Канал [" + response.getTitle() + "] с параметром " + response.getDestination() + " ранее уже был добавлен в приложение", HttpStatus.BAD_REQUEST);
                }
            }
            Optional<SlackChannelDestination> slackDestination = slackChannelDestinationRepository.findByTitle(response.getDestination());
            if (slackDestination.isPresent()) {
                channel.getSlackChannelDestination().add(slackDestination.get());
                slackChannelRepository.save(channel);
                log.debug("Successful adding channel [" + response.getTitle() + "] with parameter " + response.getDestination() + " in database");
                return new ResponseEntity<>("Канал " + response.getTitle() + " c параметром " + response.getDestination() + " успешно был создан.", HttpStatus.OK);
            } else {
                log.debug("Failed creating SlackChannel [" + response.getTitle() + "] parameter [" + response.getDestination() + " not found in database");
                throw new WWTHResourceNotFoundException("Параметр " + response.getDestination() + " не был найден.");
            }
        }
    }
    public ResponseEntity<?> deleteChannel(SlackChannelRequest request) {
        ResponseEntity<?> slackChannel = findByChannelId(request);
        if (slackChannel.getStatusCodeValue() == HttpStatus.OK.value()) {
            SlackChannel channel = (SlackChannel) slackChannel.getBody();
            assert channel != null;
            slackChannelRepository.delete(channel);
            log.info("Slack channel [" +channel.getTitle() + "] has been deleted from database");
            return new ResponseEntity<>("Канал " + channel.getTitle() + " был удалён", HttpStatus.OK);
        }
        return slackChannel;
    }

    public ResponseEntity<?> findByChannelId(SlackChannelRequest request) {
        Optional<SlackChannel> slackChannel = slackChannelRepository.findByChannelId(request.getChannelId());
        if (slackChannel.isPresent()) {
            return new ResponseEntity<>(slackChannel.get(), HttpStatus.OK);
        } else {
            log.debug("SlackChannel [" + request.getChannelId() + " not found in database");
            throw new WWTHResourceNotFoundException("Канала с id " + request.getChannelId() + " не было найдено.");
        }
    }

    public ResponseEntity<?> changeSlackChannelDestination(SlackChannelRequest request) {
        ResponseEntity<?> responseEntity = findByChannelId(request);
        if (responseEntity.getStatusCode().value() == HttpStatus.OK.value()) {
            SlackChannel slackChannel = (SlackChannel) responseEntity.getBody();
            assert slackChannel != null;
            Set<SlackChannelDestination> slackDestination = slackChannel.getSlackChannelDestination();
            for (SlackChannelDestination destination : slackDestination) {
                if (destination.getTitle().equals(request.getDestination())) {
                    log.debug("ChangeSlackChannel will be failed. Channel [" + slackChannel.getTitle() + "] " +
                            "with parameter " + request.getDestination() + " already exists in database");
                    return new ResponseEntity<>("Канал [" + slackChannel.getTitle() + "] с параметром "
                            + request.getDestination() + " ранее уже был добавлен в приложение", HttpStatus.BAD_REQUEST);
                }
            }
            Optional<SlackChannelDestination> slackChannelDestination = slackChannelDestinationRepository.findByTitle(request.getDestination());
            if (slackChannelDestination.isPresent()) {
                slackDestination.add(slackChannelDestination.get());
                slackChannel.setSlackChannelDestination(slackDestination);
                slackChannelRepository.save(slackChannel);
                log.debug("Successful adding channel [" + slackChannel.getTitle() + "] with parameter " + request.getDestination() + " in database");
                return new ResponseEntity<>("Канал " + slackChannel.getTitle() + " c параметром " + request.getDestination() + " успешно был создан.", HttpStatus.OK);
            } else {
                log.debug("SlackChannelDestination [" + request.getDestination() + " not found in database");
                throw new WWTHResourceNotFoundException("Параметр " + request.getDestination() + " не был найден.");
            }
        }
        return responseEntity;
    }


}
