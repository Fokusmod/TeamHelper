package ru.geekbrains.WowVendorTeamHelper.model;

import lombok.Data;
import ru.geekbrains.WowVendorTeamHelper.dto.WowEventTypeDTO;

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

    public static WowEventTypeDTO makeDto(WowEventType type){
        return new WowEventTypeDTO(type);
    }
}
