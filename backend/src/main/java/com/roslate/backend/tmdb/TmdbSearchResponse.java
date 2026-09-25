package com.roslate.backend.tmdb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * TMDB's answer to a movie search: one page of results, plus how many pages and results exist in total.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record TmdbSearchResponse(
        int page,
        List<TmdbMovieSummary> results,
        @JsonProperty("total_pages") int totalPages,
        @JsonProperty("total_results") int totalResults) {
}