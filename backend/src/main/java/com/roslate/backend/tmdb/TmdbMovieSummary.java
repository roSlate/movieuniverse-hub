package com.roslate.backend.tmdb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * One film in TMDB's search results. Only the fields we use are listed; TMDB sends many more, which are ignored.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record TmdbMovieSummary(
        long id,
        String title,
        @JsonProperty("release_date") String releaseDate,
        @JsonProperty("poster_path") String posterPath,
        String overview,
        @JsonProperty("vote_average") double voteAverage,
        @JsonProperty("vote_count") int voteCount) {
}