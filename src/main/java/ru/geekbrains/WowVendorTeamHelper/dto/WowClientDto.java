package ru.geekbrains.WowVendorTeamHelper.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WowClientDto {
    private Long id;
    private String orderCode;
    private String battleTag;
    private String fraction;
    private String realm;
    private String nickname;
    private String orderDateTime;
    private String characterClass;
    private String boostMode;
    private String playingType;
    private String region;
    private String service;
    private String game;
    private String specificBosses;
    private String armoryLink;
    private String noParseInfo;
}
