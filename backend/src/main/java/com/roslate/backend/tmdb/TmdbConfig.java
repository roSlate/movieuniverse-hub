package com.roslate.backend.tmdb;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;

/**
 * Creates the web client used to talk to TMDB, already set up with TMDB's address and our access token.
 */
@Configuration
public class TmdbConfig {

    @Bean
    public RestClient tmdbRestClient(@Value("${tmdb.base-url}") String baseUrl,
                                     @Value("${tmdb.access-token}") String accessToken) {
        return restClientBuilder(baseUrl, accessToken).build();
    }

    static RestClient.Builder restClientBuilder(String baseUrl, String accessToken) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
    }
}