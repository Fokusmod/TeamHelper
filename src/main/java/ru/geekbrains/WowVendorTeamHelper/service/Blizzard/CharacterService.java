package ru.geekbrains.WowVendorTeamHelper.service.Blizzard;


import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import ru.geekbrains.WowVendorTeamHelper.exeptions.WWTHErrorException;
import ru.geekbrains.WowVendorTeamHelper.exeptions.WWTHResourceNotFoundException;

@Service
@RequiredArgsConstructor
public class CharacterService {

    private final OAuth2RestTemplate restTemplate;
    private final EndpointsResolver endpointsResolver;

    public Profile forCharacter(CharacterIdentity characterIdentity) {
        String endpoint = endpointsResolver.forRegion(characterIdentity.getRegion())
                .characterWow(characterIdentity.getRealm(), characterIdentity.getCharacter(), characterIdentity.getNamespace());
        try {
            String characterData = restTemplate.getForObject(endpoint, String.class);
            JSONObject jsonObject = new JSONObject(characterData);
            Profile profile = new Profile();
            profile.setName(jsonObject.getString("name"));
            profile.setRealm(jsonObject.getJSONObject("realm").getJSONObject("name").getString("en_US"));
            profile.setFraction(jsonObject.getJSONObject("faction").getJSONObject("name").getString("en_US"));
            profile.setRace(jsonObject.getJSONObject("race").getJSONObject("name").getString("en_US"));
            profile.setCharacterClass(jsonObject.getJSONObject("character_class").getJSONObject("name").getString("en_US"));
            profile.setSpecialization(jsonObject.getJSONObject("active_spec").getJSONObject("name").getString("en_US"));
            return profile;
        } catch (HttpClientErrorException.NotFound e) {
            throw new WWTHResourceNotFoundException(e.getStatusText() + " " + characterIdentity.getCharacter() + " " + characterIdentity.getRealm());
        } catch (HttpClientErrorException e) {
            throw new WWTHErrorException(e.getStatusText());
        }
    }


}
