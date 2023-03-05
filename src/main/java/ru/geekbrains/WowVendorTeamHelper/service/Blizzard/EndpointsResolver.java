package ru.geekbrains.WowVendorTeamHelper.service.Blizzard;

import ru.geekbrains.WowVendorTeamHelper.utils.emuns.Region;

public class EndpointsResolver {

    public Endpoints forRegion(Region region) {
        return new Endpoints("https://" + region.name().toLowerCase() + ".api.blizzard.com");
    }
}
