package ru.geekbrains.WowVendorTeamHelper.model;

public class Endpoints {

    private final String baseUrl;

    public Endpoints(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String characterWow(String realm, String character, String namespace) {
        return baseUrl + "/profile/wow/character/" + realm + "/" + character + "?namespace=" + namespace;
    }



//    https://eu.api.blizzard.com/profile/wow/character/stormscale/mallygun?namespace=profile-eu&locale=en-gb

}
