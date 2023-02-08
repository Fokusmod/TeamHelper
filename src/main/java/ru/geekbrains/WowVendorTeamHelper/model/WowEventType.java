package ru.geekbrains.WowVendorTeamHelper.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "wow_events_types")
public class WowEventType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title")
    private String title;
}
