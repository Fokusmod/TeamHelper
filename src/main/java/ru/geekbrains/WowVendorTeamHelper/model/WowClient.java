package ru.geekbrains.WowVendorTeamHelper.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;

import javax.persistence.*;
import java.util.Objects;


@Entity
@Getter
@Setter
@Table(name = "wow_clients")
public class WowClient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
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
    @Column(name = "boost_mode")
    private String boostMode;
    @Column(name = "playing_type")
    private String playingType;
    @Column(name = "region")
    private String region;
    @Column(name = "service")
    private String service;
    @Column(name = "game")
    private String game;
    @Column(name = "specific_bosses")
    private String specificBosses;
    @Column(name = "armory_link")
    private String armoryLink;
    @Column(name = "no_parse_info")
    private String noParseInfo;

    private String orderStatus;
    @Column(name = "comments")
    private String comments;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass() && !getClass().isAssignableFrom(o.getClass())) return false;
        WowClient wowClient = (WowClient) (o instanceof WowClient ? o : ((HibernateProxy) o).getHibernateLazyInitializer().getImplementation());
        return Objects.equals(id, wowClient.id) &&
                Objects.equals(orderCode, wowClient.orderCode);

    }

    @Override
    public int hashCode() {
        return Objects.hash(id, orderCode);
    }

    @Override
    public String toString() {
        return "WowClient{" +
                "id=" + id +
                ", orderCode='" + orderCode + '\'' +
                ", battleTag='" + battleTag + '\'' +
                ", fraction='" + fraction + '\'' +
                ", realm='" + realm + '\'' +
                ", nickname='" + nickname + '\'' +
                ", orderDateTime='" + orderDateTime + '\'' +
                ", characterClass='" + characterClass + '\'' +
                ", boostMode='" + boostMode + '\'' +
                ", playingType='" + playingType + '\'' +
                ", region='" + region + '\'' +
                ", service='" + service + '\'' +
                ", game='" + game + '\'' +
                ", specificBosses='" + specificBosses + '\'' +
                ", armoryLink='" + armoryLink + '\'' +
                ", noParseInfo='" + noParseInfo + '\'' +
                ", orderStatus='" + orderStatus + '\'' +
                ", comments='" + comments + '\'' +
                '}';
    }

}
