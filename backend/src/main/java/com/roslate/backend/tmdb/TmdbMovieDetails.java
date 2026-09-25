package com.roslate.backend.tmdb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * The full details of one film from TMDB. Only the fields we use are listed; TMDB sends many more, which are ignored.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record TmdbMovieDetails(
        long id,
        String title,
        String overview,
        List<Genre> genres,
        Integer runtime,
        @JsonProperty("release_date") String releaseDate,
        @JsonProperty("poster_path") String posterPath,
        @JsonProperty("vote_average") double voteAverage,
        @JsonProperty("vote_count") int voteCount) {

    /**
     * A genre such as "Science Fiction".
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Genre(int id, String name) {
    }
}