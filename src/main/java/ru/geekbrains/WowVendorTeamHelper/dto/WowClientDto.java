package ru.geekbrains.WowVendorTeamHelper.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.geekbrains.WowVendorTeamHelper.model.Bundle;
import ru.geekbrains.WowVendorTeamHelper.model.ClientsBundleStages;
import ru.geekbrains.WowVendorTeamHelper.model.OrderStatus;
import ru.geekbrains.WowVendorTeamHelper.model.WowClient;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Setter
@Getter
@ToString
@NoArgsConstructor
public class WowClientDto {

    private Long id;

    private String bundle;

    private String mode;

    private String orderType;

    private String addInfo;

    private String orderCount;

    private Bundle bundleType;

    private List<ClientsBundleStagesDto> clientsBundleStages;

    private String orderCode;

    private String battleTag;

    private String fraction;

    private String realm;

    private String nickname;

    private String orderDateTime;

    private String characterClass;

    private String blizzardApiClass;

    private String boostMode;

    private String playingType;

    private String region;

    private String service;

    private String discountInfo;

    private String game;

    private String specificBosses;

    private String armoryLink;

    private String noParseInfo;

    private String originInfo;

    private OrderStatus orderStatus;

    private String ts;

    private String orderComments;

    public WowClientDto(WowClient wowClient) {
        this.id = wowClient.getId();
        this.bundle = wowClient.getBundle();
        this.mode = wowClient.getMode();
        this.orderType = wowClient.getOrderType();
        this.addInfo = wowClient.getAddInfo();
        this.orderCount = wowClient.getOrderCount();
        this.bundleType = wowClient.getBundleType();
        if (!(wowClient.getClientsBundleStages() == null)) {
            this.clientsBundleStages = wowClient.getClientsBundleStages().stream().map(ClientsBundleStagesDto::new).collect(Collectors.toList());
        } else {
            this.clientsBundleStages = new ArrayList<>();
        }
        this.orderCode = wowClient.getOrderCode();
        this.battleTag = wowClient.getBattleTag();
        this.fraction = wowClient.getFraction();
        this.realm = wowClient.getRealm();
        this.nickname = wowClient.getNickname();
        this.orderDateTime = wowClient.getOrderDateTime();
        this.characterClass = wowClient.getCharacterClass();
        this.boostMode = wowClient.getBoostMode();
        this.playingType = wowClient.getPlayingType();
        this.region = wowClient.getRegion();
        this.service = wowClient.getService();
        this.game = wowClient.getGame();
        this.specificBosses = wowClient.getSpecificBosses();
        this.armoryLink = wowClient.getArmoryLink();
        this.noParseInfo = wowClient.getNoParseInfo();
        this.originInfo = wowClient.getOriginInfo();
        this.orderStatus = wowClient.getOrderStatus();
        this.orderComments = wowClient.getOrderComments();
        this.ts = wowClient.getTs();
        this.discountInfo = wowClient.getDiscountInfo();
        this.blizzardApiClass = wowClient.getBlizzardApiClass();
    }
}
