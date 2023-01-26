package ru.geekbrains.WowVendorTeamHelper.model;

public enum TeamChannelId {

    EU_TEAM("C04M06CJWRE"),
    RV_TEAM("C04LP4M9K3K"),
    TEST("C04K0GF2H9Q"),
    US_TD_TEAM("C04LP4S25FT"),
    US_NT1_TEAM("C04LACBL27M"),
    US_NT2_TEAM("C04LD05M4A0");

    TeamChannelId(String value) {
        this.value = value;
    }

    private final String value;

    public String getValue() {
        return value;
    }
}
