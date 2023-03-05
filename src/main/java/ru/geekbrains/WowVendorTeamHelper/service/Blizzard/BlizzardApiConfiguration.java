package ru.geekbrains.WowVendorTeamHelper.service.Blizzard;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;

@Configuration
@EnableOAuth2Client
public class BlizzardApiConfiguration {

    @Value("${oauth2.clientId}")
    private String clientId;

    @Value("${oauth2.clientSecret}")
    private String clientSecret;


    @Bean
    public OAuth2ProtectedResourceDetails blizzardRestApi() {
        ClientCredentialsResourceDetails details = new ClientCredentialsResourceDetails();
        details.setId("blizzardRestApi");
        details.setClientId(clientId);
        details.setClientSecret(clientSecret);
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
