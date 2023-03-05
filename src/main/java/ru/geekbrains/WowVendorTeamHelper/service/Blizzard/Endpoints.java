package ru.geekbrains.WowVendorTeamHelper.service.Blizzard;

public class Endpoints {

    private final String baseUrl;

    public Endpoints(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String characterWow(String realm, String character, String namespace) {
        return baseUrl + "/profile/wow/character/" + realm + "/" + character + "?namespace=" + namespace;
    }


}
