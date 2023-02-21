package ru.geekbrains.WowVendorTeamHelper.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(name = "bundle_stages")
public class BundleStage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title")
    private String title;

    @Override
    public String toString() {
        return "BundleStage{" +
                "id=" + id +
                ", title='" + title + '\'' +
                '}';
    }
}
