package ru.geekbrains.WowVendorTeamHelper.utils.emuns;

public enum WowClassEnum {

    WARRIOR("Warrior"),

    PALADIN("Paladin"),

    HUNTER("Hunter"),

    ROGUE("Rogue"),

    PRIEST("Priest"),

    SHAMAN("Shaman"),

    MAGE("Mage"),

    WARLOCK("Warlock"),

    MONK("Monk"),

    DRUID("Druid"),

    DEMON_HUNTER("Demon hunter"),

    DEATH_KNIGNT("Death knight"),

    EVOKER("Evoker");


    public final String value;

    WowClassEnum(String value) {
        this.value = value;
    }

}
