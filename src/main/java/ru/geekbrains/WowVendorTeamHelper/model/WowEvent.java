package ru.geekbrains.WowVendorTeamHelper.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "wow_events")
public class WowEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "type_id", referencedColumnName = "id")
    private WowEventType wowEventType;

    @OneToOne
    @JoinColumn(name = "team_id", referencedColumnName = "id")
    private Team team;

    @Column(name = "event_date")
    private String eventDate;

    @Column(name = "startedAt")
    private String startedAt;

}
