package ru.geekbrains.WowVendorTeamHelper.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;
import ru.geekbrains.WowVendorTeamHelper.model.CharacterIdentity;
import ru.geekbrains.WowVendorTeamHelper.model.EndpointsResolver;

import java.util.Optional;

public class CharacterService {

    private final RestTemplate restTemplate;

    private final EndpointsResolver endpointsResolver;

    @Autowired
    public CharacterService(RestTemplate blizzardApiRestTemplate, EndpointsResolver endpointsResolver) {
        this.restTemplate = blizzardApiRestTemplate;
        this.endpointsResolver = endpointsResolver;
    }

//    public Optional<?> forCharacter(CharacterIdentity characterIdentity) {
//        String endpoint = endpointsResolver.forRegion(characterIdentity.getRegion())
//                .characterWow(characterIdentity.getRealm(), characterIdentity.getCharacter(), characterIdentity.getNamespace());
//        System.out.println(endpoint);
////        Profile characterData = restTemplate.getForObject(endpoint, Profile.class);
//
////        assert characterData != null;
//        return Optional.of(characterData);
//    }


}
