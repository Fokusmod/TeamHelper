package ru.geekbrains.WowVendorTeamHelper.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import ru.geekbrains.WowVendorTeamHelper.exeptions.ResourceNotFoundException;
import ru.geekbrains.WowVendorTeamHelper.model.ClientStage;
import ru.geekbrains.WowVendorTeamHelper.model.SlackMessageInfo;
import ru.geekbrains.WowVendorTeamHelper.model.OrderStatus;
import ru.geekbrains.WowVendorTeamHelper.model.WowClient;
import ru.geekbrains.WowVendorTeamHelper.repository.WowClientRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class WowClientService {

    /**
     * Клиенты сохраняются на основе кода заказа. Если клиент сохраненный в бд имеет код &123 и в приложение прилетает
     * второй клиент с таким же кодом &123 - сохранится второй.
     * В течении часа после публикации заказа в Slack канал есть возможность отредактировать сообщение в канале. Эти изменения
     * применяться к объекту в бд.
     * В течении часа после публикации заказа в Slack канал есть возможность удалить сообщение в канале.Эти изменения
     * применяться к объекту в бд.
     * Если в канале 2 одинаковых заказа то валидным будет считаться только один.
     * Удаление обьекта происходит путём проверки его orderCode и timeStamp который генерируется Slack-ом. Это исплючает
     * возможность удаления обьекта в случае если:
     * ---1 шаг. публикация первого сообщения: Обьект1 код заказа &12345
     * ---2 шаг. публикация второго сообщения: Обьект1 код заказа &12345
     * ---3 шаг. удаление первого сообщения: Обьект1 код заказа &12345
     * ---Пояснение: По логике приложения при публикации первого сообщения происходит сохранение обьекта. Затем после
     * публикации второго сообщения происходит изменение обьекта так как они содержат одинаковый orderCode. После этого
     * происходит удаление первого сообщения, которое в случае если не было бы проверки на timeStamp привело бы к удалению
     * обьекта, или обьектов.
     * По истечению часа любое взаимодействие с обьектом из Slack канала становится не возможным.
     */

    private static final String STATUS_NEW = "NEW";
    private static final String EVENT_NEW_CLIENTS = "NEW";
    private static final String EVENT_DELETE_CLIENTS = "DELETE";
    private static final String EVENT_CHANGE_CLIENTS = "CHANGE";
    private final OrderParser orderParser;
    private final WowClientRepository wowClientRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final OrderStatusService orderStatusService;

    public void saveClient(WowClient wowClient) {
        Optional<WowClient> existsClient = wowClientRepository.findByOrderCode(wowClient.getOrderCode());
        if (existsClient.isPresent()) {
            WowClient client = existsClient.get();
            wowClient.setId(client.getId());
            wowClient.setClientBundleStage(client.getClientBundleStage());
            wowClient.setOrderStatus(client.getOrderStatus());
        }
        wowClientRepository.save(wowClient);
    }


    public void sendMessageToTopic(String message) {
        messagingTemplate.convertAndSend("/events/message", message);
    }


    public void saveParseClients(SlackMessageInfo slackMessageInfo) {
        List<WowClient> list = orderParser.stringParser(slackMessageInfo.getText());
        OrderStatus statusNew = orderStatusService.getOrderStatusByTitle(STATUS_NEW);
        for (WowClient wowClient : list) {
            wowClient.setTs(slackMessageInfo.getTs());
            if (wowClient.getOrderStatus() == null) {
                wowClient.setOrderStatus(statusNew);
            }
            saveClient(wowClient);
        }
        sendMessageToTopic(EVENT_NEW_CLIENTS);
    }

    public List<WowClient> getNewClients() {
        OrderStatus orderStatus = orderStatusService.getOrderStatusByTitle(STATUS_NEW);
        Comparator<WowClient> byId = Comparator.comparingLong(WowClient::getId);
        List<WowClient> clients = wowClientRepository.getWowClientByOrderStatus(orderStatus);
        clients.sort(byId);
        return clients;
    }

    private WowClient findById(Long id) {
        return wowClientRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Пользователь c идентификатором - " + id + " не найден"));
    }

    public WowClient setComments(Long id, String comments) {
        WowClient wowClient = findById(id);
        if (comments.equals("null")) {
            wowClient.setOrderComments("");
            wowClientRepository.save(wowClient);
        } else {
            wowClient.setOrderComments(comments);
            wowClientRepository.save(wowClient);
        }
        log.debug("Комментарий к заказу [" + wowClient.getOrderCode() + "] был изменен.");
        return wowClient;
    }

    public List<WowClient> changeOrder(Long id, String orderInfo) {
        WowClient wowClient = findById(id);
        OrderStatus currentStatus = wowClient.getOrderStatus();
        List<ClientStage> clientStage = wowClient.getClientBundleStage();

        List<WowClient> wowClients = orderParser.stringParser(orderInfo);
        for (WowClient order : wowClients) {
            order.setId(id);
            order.setClientBundleStage(clientStage);
            order.setOrderStatus(currentStatus);
            saveClient(order);
        }
        log.debug("Заказ [" + wowClient.getOrderCode() + "] был изменен через приложение.");
        return wowClients;
    }


    public void deleteWowClientFromSlack(SlackMessageInfo slackMessageInfo) {
        boolean thereAreMatches = false;
        List<WowClient> clientList = wowClientRepository.getAllByTs(slackMessageInfo.getTs());
        if (!clientList.isEmpty()) {
            for (WowClient wowClient : clientList) {
                wowClientRepository.deleteById(wowClient.getId());
                thereAreMatches = true;
                log.debug("Заказ [" + wowClient.getOrderCode()+ "] был удален пользователем Slack канала.");
            }
        }
        if (thereAreMatches) {
            sendMessageToTopic(EVENT_DELETE_CLIENTS);

        }
    }

    public void changeWowClientFromSlack(SlackMessageInfo slackMessageInfo) {
        boolean thereAreMatches = false;
        List<WowClient> clientList = orderParser.stringParser(slackMessageInfo.getText());
        for (WowClient wowClient : clientList) {
            Optional<WowClient> existsClient = wowClientRepository.findByOrderCodeAndTs(wowClient.getOrderCode(), slackMessageInfo.getTs());
            if (existsClient.isPresent()) {
                saveClient(wowClient);
                thereAreMatches = true;
                log.debug("Заказ [" + wowClient.getOrderCode()+ "] был изменен пользователем Slack канала.");
            }
        }
        if (thereAreMatches) sendMessageToTopic(EVENT_CHANGE_CLIENTS);
    }
}
