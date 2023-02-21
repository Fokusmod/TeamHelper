package ru.geekbrains.WowVendorTeamHelper.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(name = "bundles")
public class Bundle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "title")
    private String title;

    @ManyToMany
    @Fetch(FetchMode.JOIN)
    @JoinTable(name = "bundle_with_bundle_stages",
            joinColumns = @JoinColumn(name = "bundle_id"),
            inverseJoinColumns = @JoinColumn(name = "bundle_stage_id"))
    private List<BundleStage> stages = new ArrayList<>();

    @Override
    public String toString() {
        return "Bundle{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", stages=" + stages +
                '}';
    }
}
