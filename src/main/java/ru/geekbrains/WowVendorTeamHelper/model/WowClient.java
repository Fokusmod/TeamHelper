package ru.geekbrains.WowVendorTeamHelper.model;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;


@Entity
@ToString
@Getter
@Setter
@Table(name = "wow_clients")
public class WowClient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "bundle")
    private String bundle;

    @Column(name = "order_mode")
    private String mode;

    @Column(name = "order_type")
    private String orderType;

    @Column(name = "add_info")
    private String addInfo;

    @Column(name = "order_count")
    private String orderCount;

    @OneToOne
    @JoinColumn(name = "bundle_id", referencedColumnName = "id")
    private Bundle bundleType;

    @OneToMany(mappedBy = "client")
    private List<ClientsBundleStages> clientsBundleStages;
    @NaturalId(mutable = true)
    @Column(name = "order_code")
    private String orderCode;

    @Column(name = "battle_tag")
    private String battleTag;

    @Column(name = "fraction")
    private String fraction;

    @Column(name = "realm")
    private String realm;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "order_date_time")
    private String orderDateTime;

    @Column(name = "character_class")
    private String characterClass;

    @Column(name = "blizzard_class")
    private String blizzardApiClass;

    @Column(name = "boost_mode")
    private String boostMode;

    @Column(name = "playing_type")
    private String playingType;

    @Column(name = "region")
    private String region;

    @Column(name = "service")
    private String service;

    @Column(name = "discount")
    private String discountInfo;

    @Column(name = "game")
    private String game;

    @Column(name = "specific_bosses")
    private String specificBosses;

    @Column(name = "armory_link")
    private String armoryLink;

    @Column(name = "no_parse_info")
    private String noParseInfo;

    @Column(name = "origin_info")
    private String originInfo;

    @OneToOne
    @JoinColumn(name = "status_id", referencedColumnName = "id")
    private OrderStatus orderStatus;

    @Column(name = "order_comments")
    private String orderComments;

    @Column(name = "order_ts")
    private String ts;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) {
            return false;
        }
        if (obj instanceof WowClient) {
            return ((WowClient) obj).getOrderCode().equals(getOrderCode());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderCode);
    }



}

