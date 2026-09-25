package com.roslate.backend.rating;

import java.util.Optional;

/**
 * The result of combining the TMDB rating with the ratings of the app's users.
 *
 * @param score       the combined rating from 0 to 10, or empty when there are no votes at all
 * @param totalVotes  how many votes the score is based on (TMDB votes plus app votes)
 * @param explanation a short text saying what the score is based on, ready to show on the film page
 */
public record CombinedRating(Optional<Double> score, int totalVotes, String explanation) {
}