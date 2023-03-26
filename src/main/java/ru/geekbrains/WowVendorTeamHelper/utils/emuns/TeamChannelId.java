package ru.geekbrains.WowVendorTeamHelper.utils.emuns;

public enum TeamChannelId {
    TEST("C0504RGHDJQ");

    TeamChannelId(String value) {
        this.value = value;
    }

    private final String value;

    public String getValue() {
        return value;
    }
}
