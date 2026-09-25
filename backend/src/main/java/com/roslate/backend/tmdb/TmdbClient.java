package com.roslate.backend.tmdb;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Talks to the TMDB API: searches films by title and fetches the details of one film.
 * Errors from TMDB, such as a rejected token, are thrown as exceptions.
 */
@Component
public class TmdbClient {

    private static final String LANGUAGE = "pt-PT";

    private final RestClient restClient;

    public TmdbClient(RestClient tmdbRestClient) {
        this.restClient = tmdbRestClient;
    }

    /**
     * Searches films by title.
     *
     * @param title the text to search for
     * @return the first page of matching films
     */
    public TmdbSearchResponse searchMovies(String title) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder.path("/search/movie")
                        .queryParam("query", "{title}")
                        .queryParam("language", LANGUAGE)
                        .build(title))
                .retrieve()
                .body(TmdbSearchResponse.class);
    }

    /**
     * Fetches the details of one film.
     *
     * @param tmdbId the film's TMDB id
     * @return the film's details
     */
    public TmdbMovieDetails getMovie(long tmdbId) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder.path("/movie/{id}")
                        .queryParam("language", LANGUAGE)
                        .build(tmdbId))
                .retrieve()
                .body(TmdbMovieDetails.class);
    }
}
