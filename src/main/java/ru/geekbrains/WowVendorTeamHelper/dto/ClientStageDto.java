package ru.geekbrains.WowVendorTeamHelper.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.geekbrains.WowVendorTeamHelper.model.ClientStage;

@Setter
@Getter
@ToString
@NoArgsConstructor
public class ClientStageDto {

    private Long id;

    private String title;

    public ClientStageDto(ClientStage clientStage) {
        this.id = clientStage.getId();
        this.title = clientStage.getTitle();
    }
}
