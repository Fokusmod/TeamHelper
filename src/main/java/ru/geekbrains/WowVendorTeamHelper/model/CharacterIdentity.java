package ru.geekbrains.WowVendorTeamHelper.model;

import java.util.Objects;

public class CharacterIdentity {

    private final Region region;

    private final String realm;

    private final String character;

    private final String namespace;

    private CharacterIdentity(Region region, String realm, String character, String namespace) {
        this.region = region;
        this.realm = realm;
        this.character = character;
        this.namespace = namespace;
    }

    public static CharacterIdentity of(Region region, String realm, String character, String namespace) {
        return new CharacterIdentity(region, realm, character, namespace);
    }

    public Region getRegion() {
        return region;
    }

    public String getRealm() {
        return realm;
    }

    public String getCharacter() {
        return character;
    }

    public String getNamespace() {
        return namespace;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CharacterIdentity that = (CharacterIdentity) o;
        return Objects.equals(region, that.region) &&
                Objects.equals(realm, that.realm) &&
                Objects.equals(character, that.character)&&
                Objects.equals(namespace, that.namespace);
    }

    @Override
    public int hashCode() {
        return Objects.hash(region, realm, character, namespace);
    }

}
