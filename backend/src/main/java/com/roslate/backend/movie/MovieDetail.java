package com.roslate.backend.movie;

import java.util.List;

/**
 * Everything shown on a film's page: the description, the TMDB rating, the ratings of the app's users and the
 * combined rating.
 *
 * @param tmdbId              the film's TMDB id
 * @param title               the film's title
 * @param overview            the synopsis; may be empty when TMDB has none in our language
 * @param genres              the names of the film's genres
 * @param runtimeMinutes      the duration in minutes, or {@code null} when unknown
 * @param year                the release year, or {@code null} when unknown
 * @param posterUrl           the full address of the poster image, or {@code null} when there is none
 * @param tmdbAverage         the TMDB average rating; only meaningful when {@code tmdbVotes} is above zero
 * @param tmdbVotes           the number of TMDB votes; zero means "sem votos"
 * @param appAverage          the average of the app users' ratings; only meaningful when {@code appVotes} is above zero
 * @param appVotes            the number of app users' ratings
 * @param combinedScore       the combined rating from 0 to 10, or {@code null} when there is not enough information
 * @param combinedVotes       how many votes the combined rating is based on
 * @param combinedExplanation a short text saying what the combined rating is based on
 */
public record MovieDetail(long tmdbId, String title, String overview, List<String> genres,
                          Integer runtimeMinutes, Integer year, String posterUrl,
                          double tmdbAverage, int tmdbVotes,
                          double appAverage, int appVotes,
                          Double combinedScore, int combinedVotes, String combinedExplanation) {
}