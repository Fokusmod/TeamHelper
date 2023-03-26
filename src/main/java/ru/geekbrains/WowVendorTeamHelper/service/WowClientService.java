package ru.geekbrains.WowVendorTeamHelper.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.geekbrains.WowVendorTeamHelper.dto.ClientBundleRequest;
import ru.geekbrains.WowVendorTeamHelper.exeptions.WWTHResourceNotFoundException;
import ru.geekbrains.WowVendorTeamHelper.model.*;
import ru.geekbrains.WowVendorTeamHelper.repository.ClientsBundleStagesRepository;
import ru.geekbrains.WowVendorTeamHelper.repository.WowClientRepository;
import ru.geekbrains.WowVendorTeamHelper.service.Blizzard.CharacterIdentity;
import ru.geekbrains.WowVendorTeamHelper.service.Blizzard.CharacterService;
import ru.geekbrains.WowVendorTeamHelper.service.Blizzard.Profile;
import ru.geekbrains.WowVendorTeamHelper.utils.emuns.Region;

import javax.persistence.Cacheable;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;

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
    public static final String EVENT_CHANGE_CLIENTS = "CHANGE";

    private static final String MESSAGE = "MESSAGE";
    private final OrderParser orderParser;
    private final WowClientRepository wowClientRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final OrderStatusService orderStatusService;
    private final ClientsBundleStagesRepository clientsBundleStagesRepository;
    private final BundleService bundleService;
    private final CharacterService characterService;
    @PersistenceContext
    private final EntityManager entityManager;
    private int count = 0;

    @Transactional
    public void setClientStages(Long id, ClientBundleRequest clientBundleRequest) {
        OrderStatus orderStatus = orderStatusService.getOrderStatusByTitle(STATUS_NEW);
        WowClient wowClient = findById(id);
        if (!wowClient.getClientsBundleStages().isEmpty()) {
            List<ClientsBundleStages> stages = clientsBundleStagesRepository.findByClientId(wowClient.getId());
            clientsBundleStagesRepository.deleteAll(stages);
            entityManager.flush();
        }
        Bundle bundle = bundleService.findById(clientBundleRequest.getId());

        if (wowClient.getOrderCount()!= null) {
            String orderCount = wowClient.getOrderCount().substring(1);
            int count = Integer.parseInt(orderCount);

            List<ClientsBundleStages> copies = new ArrayList<>();
            for (BundleStage stage : bundle.getStages()) {
                for (int i = 0; i < count; i++) {
                    ClientsBundleStages newClientsBundleStages = new ClientsBundleStages();
                    newClientsBundleStages.setClient(wowClient);
                    newClientsBundleStages.setBundleStage(stage);
                    newClientsBundleStages.setOrderStatus(orderStatus);
                    copies.add(clientsBundleStagesRepository.save(newClientsBundleStages));
                }
            }
            wowClient.setClientsBundleStages(copies);
            wowClient.setBundleType(bundle);
            wowClientRepository.saveAndFlush(wowClient);
            sendMessageToTopic(EVENT_CHANGE_CLIENTS + "/" + wowClient.getId());
        } else {
            List<ClientsBundleStages> clientsBundleStages = new ArrayList<>();
            for (BundleStage stage : bundle.getStages()) {
                ClientsBundleStages newClientsBundleStages = new ClientsBundleStages();
                newClientsBundleStages.setClient(wowClient);
                newClientsBundleStages.setBundleStage(stage);
                newClientsBundleStages.setOrderStatus(orderStatus);
                clientsBundleStages.add(clientsBundleStagesRepository.save(newClientsBundleStages));
            }
            wowClient.setClientsBundleStages(clientsBundleStages);
            wowClient.setBundleType(bundle);
            wowClientRepository.saveAndFlush(wowClient);
            sendMessageToTopic(EVENT_CHANGE_CLIENTS + "/" + wowClient.getId());
        }
    }


    public List<WowClient> findOrdersByOrderOrBattleTagOrId(String request) {
        List<WowClient> result = new ArrayList<>();
        if (request.contains("&")) {
            Optional<WowClient> existCode = wowClientRepository.findByOrderCode(request);
            existCode.ifPresent(result::add);
            return result;
        } else if (request.contains("#")) {
            result.addAll(wowClientRepository.getByBattleTag(request));
            return result;
        } else {
            try {
                Long requestId = Long.parseLong(request);
                Optional<WowClient> existId = wowClientRepository.findById(requestId);
                existId.ifPresent(result::add);
                return result;
            } catch (NumberFormatException e) {
                return result;
            }
        }
    }

    public void saveClient(WowClient wowClient) {
        Optional<WowClient> existsClient = wowClientRepository.findByOrderCode(wowClient.getOrderCode());
        if (existsClient.isPresent()) {
            WowClient client = existsClient.get();
            wowClient.setId(client.getId());
        }
        wowClientRepository.saveAndFlush(wowClient);
    }

    public WowClient getClientById (Long id) {
       return wowClientRepository.getById(id);
    }


    public synchronized void sendMessageToTopic(String message) {
        messagingTemplate.convertAndSend("/events/message", message);
    }


    public void saveParseClients(SlackMessageInfo slackMessageInfo) {
        try {
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
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void setCharacterClass(WowClient wowClient) {
        try {
            String clientRegion = wowClient.getRegion();
            Region[] regions = Region.values();
            for (Region region : regions) {
                if (clientRegion.contains(region.name().toLowerCase())) {
                    Profile profile = characterService.forCharacter(CharacterIdentity.of(region, wowClient.getRealm().toLowerCase().replace(" ", "-")
                            , wowClient.getNickname().toLowerCase(), "profile-" + region));
                    wowClient.setBlizzardApiClass("(" + profile.getSpecialization() + " " +profile.getCharacterClass()  + ")");
                    count++;
                    return;
                }
            }
        } catch (Exception e) {
            log.debug("Не удалось узнать класс персонажа, вероятно никнейм и сервер не корректны.");
        }

    }
    public List<WowClient> getNewClients() {
        entityManager.getEntityManagerFactory().getCache().evictAll();
        OrderStatus orderStatus = orderStatusService.getOrderStatusByTitle(STATUS_NEW);
        Comparator<WowClient> byId = Comparator.comparingLong(WowClient::getId);
        List<WowClient> clients = wowClientRepository.getWowClientByOrderStatus(orderStatus);
        for (WowClient client : clients) {
            if (client.getCharacterClass() == null && client.getBlizzardApiClass() == null) {
                setCharacterClass(client);
                saveClient(client);
            } else if (client.getBlizzardApiClass() != null && client.getCharacterClass() != null) {
                client.setBlizzardApiClass(null);
            }
        }
        clients.sort(byId);
        return clients;
    }

    private WowClient findById(Long id) {
        return wowClientRepository.findById(id).orElseThrow(() -> new WWTHResourceNotFoundException("Пользователь c идентификатором - " + id + " не найден"));
    }

    public WowClient setComments(Long id, String comments) {
        WowClient wowClient = findById(id);
        if (comments.equals("null")) {
            wowClient.setOrderComments("");
            wowClientRepository.saveAndFlush(wowClient);
        } else {
            wowClient.setOrderComments(comments);
            wowClientRepository.saveAndFlush(wowClient);
        }
        log.debug("Комментарий к заказу [" + wowClient.getOrderCode() + "] был изменен.");
        return wowClient;
    }

    @Transactional
    public List<WowClient> changeOrder(Long id, String orderInfo) {
        WowClient wowClient = findById(id);
        OrderStatus currentStatus = wowClient.getOrderStatus();
        List<ClientsBundleStages> clientStage = wowClient.getClientsBundleStages();
        List<WowClient> wowClients = orderParser.stringParser(orderInfo);
        for (WowClient order : wowClients) {
            order.setId(id);
            setCharacterClass(order);
            order.setTs(wowClient.getTs());
            if (order.getBundle().equals(wowClient.getBundle())) {
                order.setBundleType(wowClient.getBundleType());
                order.setClientsBundleStages(wowClient.getClientsBundleStages());
            } else if (order.getBundle().equals("false") && wowClient.getBundle().equals("true")) {
                order.setBundleType(null);
                deleteClientBundleStages(wowClient);
                log.debug("Заказ " + order.getOrderCode() + " был изменён в Slack (больше не является Bundle)");
            } else if (order.getBundle().equals("true") && wowClient.getBundle().equals("false")) {
                log.debug("Заказ " + wowClient.getOrderCode() + " был изменён в Slack (теперь является Bundle)");
            }
            order.setOrderStatus(currentStatus);
            order.setOrderComments(wowClient.getOrderComments());
            saveClient(order);
        }
        log.debug("Заказ [" + wowClient.getOrderCode() + "] был изменен через приложение.");
        return wowClients;
    }

    public void deleteClientBundleStages(WowClient wowClient) {
        List<ClientsBundleStages> list = clientsBundleStagesRepository.findByClientId(wowClient.getId());
        if (list.size() != 0) {
            clientsBundleStagesRepository.deleteAllInBatch(list);
            entityManager.flush();
        }
    }


    @Transactional
    public void deleteWowClientFromSlack(SlackMessageInfo slackMessageInfo) {
        try {
            boolean thereAreMatches = false;
            List<WowClient> clientList = wowClientRepository.getAllByTs(slackMessageInfo.getTs());
            if (!clientList.isEmpty()) {
                for (WowClient wowClient : clientList) {
                    deleteClientBundleStages(wowClient);
                    wowClientRepository.deleteById(wowClient.getId());
                    entityManager.flush();
                    thereAreMatches = true;
                    log.debug("Заказ [" + wowClient.getOrderCode() + "] был удален пользователем Slack канала.");
                }
            }
            if (thereAreMatches) {
                sendMessageToTopic(EVENT_DELETE_CLIENTS);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }

    }

    @Transactional
    public void changeWowClientFromSlack(SlackMessageInfo slackMessageInfo) {
        List<Long> ids = new ArrayList<>();
        boolean thereAreMatches = false;
        List<WowClient> clientList = orderParser.stringParser(slackMessageInfo.getText());
        List<WowClient> existsClient = wowClientRepository.getAllByTs(slackMessageInfo.getTs());
        for (WowClient wowClient : clientList) {
            for (int i = 0; i < existsClient.size(); i++) {
                if (wowClient.getOrderCode().equals(existsClient.get(i).getOrderCode())) {
                    wowClient.setTs(existsClient.get(i).getTs());
                    wowClient.setId(existsClient.get(i).getId());
                    wowClient.setOrderStatus(existsClient.get(i).getOrderStatus());
                    checkChangeBundleOrder(wowClient, existsClient.get(i));
                    wowClientRepository.saveAndFlush(wowClient);
                    thereAreMatches = true;
                    ids.add(wowClient.getId());
                    existsClient.remove(wowClient);
                    log.debug("Заказ [" + wowClient.getOrderCode() + "] был изменен пользователем Slack канала.");
                }
            }
        }
        if (!existsClient.isEmpty()) {
            thereAreMatches = true;
            for (WowClient wowClient : existsClient) {
                wowClientRepository.deleteById(wowClient.getId());
                entityManager.flush();
                log.debug("При изменении сообщения заказ [" + wowClient.getOrderCode() + "] был удален пользователем Slack канала.");
            }
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < ids.size(); i++) {
            stringBuilder.append(ids.get(i)).append(" ");
        }
        if (thereAreMatches) sendMessageToTopic(EVENT_CHANGE_CLIENTS +"/" + stringBuilder.toString().trim());
    }

    private void checkChangeBundleOrder(WowClient newClient, WowClient oldClient) {
        OrderStatus orderStatus = orderStatusService.getOrderStatusByTitle(STATUS_NEW);
        if (newClient.getBundle().equals(oldClient.getBundle())) {
            newClient.setBundleType(oldClient.getBundleType());
            newClient.setClientsBundleStages(oldClient.getClientsBundleStages());
        } else if (newClient.getBundle().equals("false") && oldClient.getBundle().equals("true")) {
            newClient.setOrderStatus(orderStatus);
            newClient.setBundleType(null);
            deleteClientBundleStages(oldClient);
            sendMessageToTopic(MESSAGE + "/" + newClient.getId() + "/" + "Заказ " + newClient.getOrderCode() +
                    " больше не является бандлом. " + "/" + "Необходимо снова определить событие для него.\n " +
                    "Причина: Заказ был изменён в Slack");
            log.debug("Заказ " + newClient.getOrderCode() + " был изменён в Slack (больше не является Bundle)");
        } else if (newClient.getBundle().equals("true") && oldClient.getBundle().equals("false")) {
            newClient.setOrderStatus(orderStatus);
            sendMessageToTopic(MESSAGE + "/" + newClient.getId() + "/" + "Заказ " + newClient.getOrderCode() +
                    " теперь является бандлом. " + "/" + " Необходимо снова определить событие для него.\n " +
                    "Причина: Заказ был изменён в Slack");
            log.debug("Заказ " + newClient.getOrderCode() + " был изменён в Slack (теперь является Bundle)");
        }
    }
}
