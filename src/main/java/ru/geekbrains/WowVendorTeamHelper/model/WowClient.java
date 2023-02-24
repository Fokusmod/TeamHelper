package ru.geekbrains.WowVendorTeamHelper.model;


import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.proxy.HibernateProxy;
import ru.geekbrains.WowVendorTeamHelper.dto.WowClientDto;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
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
    @Column(name = "bundle")
    private String bundle;

    @OneToOne
    @JoinColumn(name = "bundle_id", referencedColumnName = "id")
    private Bundle bundleType;

    @OneToMany
    @Fetch(FetchMode.SUBSELECT)
    @JoinTable(name = "clients_bundle_stages",
            joinColumns = @JoinColumn(name = "client_id"),
            inverseJoinColumns = @JoinColumn(name = "client_stage_id"))
    private List<ClientStages> clientBundleStage;

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

    @OneToOne
    @JoinColumn(name = "status_id", referencedColumnName = "id")
    private OrderStatus orderStatus;

    @Column(name = "order_comments")
    private String orderComments;

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
                ", bundle='" + bundle + '\'' +
                ", bundleType=" + bundleType +
                ", clientBundleStage=" + clientBundleStage +
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
                ", orderStatus=" + orderStatus +
                ", orderComments='" + orderComments + '\'' +
                '}';
    }

    public static List<WowClientDto> list(List<WowClient> list){
        List<WowClientDto> dtoList = new ArrayList<>();
        list.forEach(C -> {
            dtoList.add(
                    WowClientDto.builder()
                    .id(C.id)
                    .orderCode(C.orderCode)
                    .battleTag(C.battleTag)
                    .fraction(C.fraction)
                    .realm(C.realm)
                    .nickname(C.nickname)
                    .orderDateTime(C.orderDateTime)
                    .characterClass(C.characterClass)
                    .boostMode(C.boostMode)
                    .playingType(C.playingType)
                    .region(C.region)
                    .service(C.service)
                    .game(C.game)
                    .specificBosses(C.specificBosses)
                    .armoryLink(C.armoryLink)
                    .noParseInfo(C.noParseInfo)
                    .build()
            );
        });
        return dtoList;
    }
}
