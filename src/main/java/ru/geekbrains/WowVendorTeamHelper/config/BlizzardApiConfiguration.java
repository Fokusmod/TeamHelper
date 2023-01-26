package ru.geekbrains.WowVendorTeamHelper.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import ru.geekbrains.WowVendorTeamHelper.model.EndpointsResolver;

public class BlizzardApiConfiguration {

    @Value("${oauth2:userAuthorizationUri}")
    private String userAuthorizationUri;

    @Value("${oauth2:accessTokenUri}")
    private String accessTokenUri;

    @Value("${oauth2:tokenName}")
    private String tokenName;

    @Value("${oauth2:clientId}")
    private String clientId;

    @Value("${oauth2:clientSecret}")
    private String clientSecret;


    @Bean
    public OAuth2ProtectedResourceDetails blizzardRestApi() {
        ClientCredentialsResourceDetails details = new ClientCredentialsResourceDetails();
        details.setId("blizzardRestApi");
        details.setClientId("28031be62ca84e78a9ea4fb18e818c4c");
        details.setClientSecret("Y5KW9l5l34ybu8zUovSj22wb58SxS7Fj");
        details.setAccessTokenUri("https://eu.battle.net/oauth/token");
        details.setTokenName("access_token");
        return details;
    }

    @Bean
    public OAuth2RestTemplate blizzardApiRestTemplate(OAuth2ClientContext clientContext) {
        OAuth2RestTemplate restTemplate = new OAuth2RestTemplate(blizzardRestApi(), clientContext);
        restTemplate.setAccessTokenProvider(new ClientCredentialsAccessTokenProvider());
        return restTemplate;
    }

    @Bean
    public EndpointsResolver endpointsResolver() {
        return new EndpointsResolver();
    }

}
