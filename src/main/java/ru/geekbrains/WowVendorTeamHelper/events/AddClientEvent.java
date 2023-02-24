package ru.geekbrains.WowVendorTeamHelper.events;

public class AddClientEvent extends AbstractEvent {

    private Long idClient;

    public AddClientEvent(Long idClient) {
        super(idClient);
        this.idClient = idClient;
    }

    public Long getIdClient() {
        return idClient;
    }
}
