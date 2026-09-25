package com.roslate.backend.movie;

/**
 * One film in the search results shown to the user.
 *
 * @param tmdbId      the film's TMDB id, which identifies it everywhere in the app
 * @param title       the film's title
 * @param year        the release year, or {@code null} when TMDB has no release date
 * @param posterUrl   the full address of the poster image, or {@code null} when the film has no poster
 * @param voteAverage the TMDB average rating; only meaningful when {@code voteCount} is above zero
 * @param voteCount   the number of TMDB votes; zero means "sem votos"
 */
public record MovieSearchItem(long tmdbId, String title, Integer year, String posterUrl,
                              double voteAverage, int voteCount) {
}