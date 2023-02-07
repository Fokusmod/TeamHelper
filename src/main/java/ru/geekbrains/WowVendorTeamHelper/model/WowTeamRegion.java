package ru.geekbrains.WowVendorTeamHelper.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "wow_teams_regions")
public class WowTeamRegion {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;
}
