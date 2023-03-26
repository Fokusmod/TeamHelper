package ru.geekbrains.WowVendorTeamHelper.model;

import lombok.*;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

    @Column(name = "started_at")
    private String startedAt;

    @OneToOne
    @JoinColumn(name = "status_id", referencedColumnName = "id")
    private WowEventStatus wowEventStatus;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "wow_clients_with_events",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "client_id"))
    private List<WowClient> clientList = new ArrayList<>();

    @Override
    public String toString() {
        return "WowEvent{" +
                "id=" + id +
                ", wowEventType=" + wowEventType +
                ", team=" + team +
                ", eventDate='" + eventDate + '\'' +
                ", startedAt='" + startedAt + '\'' +
                ", wowEventStatus=" + wowEventStatus +
                ", clientList=" + clientList +
                '}';
    }
}
