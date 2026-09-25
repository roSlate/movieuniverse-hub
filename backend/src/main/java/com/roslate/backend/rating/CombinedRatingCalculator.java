package com.roslate.backend.rating;

import java.util.Optional;

/**
 * Calculates the combined rating of a film from the TMDB rating and the ratings of the app's users.
 * <p>
 * It is a weighted average (the same idea IMDb uses for its Top 250): the film's own average is mixed with a neutral
 * rating, and the number of votes decides how much the film's own average counts. See DECISIONS.md.
 * <p>
 * It only does arithmetic: no database, no network and no user interface.
 */
public final class CombinedRatingCalculator {

    /**
     * The rating assumed for a film before any vote is known (provisional, see DECISIONS.md).
     */
    static final double NEUTRAL_RATING = 6.5;

    /**
     * How many "imaginary" votes at the neutral rating are added to the real ones (provisional).
     */
    static final int NEUTRAL_WEIGHT = 500;

    private CombinedRatingCalculator() {
    }

    /**
     * Combines the TMDB rating and the app users' rating into one score, weighting each by its number of votes.
     * A source with no votes contributes nothing, so its average is ignored.
     *
     * @param tmdbAverage the TMDB average, from 0 to 10
     * @param tmdbVotes   the number of TMDB votes
     * @param appAverage  the average of the app users' ratings, from 1 to 10
     * @param appVotes    the number of app users' ratings
     * @return the score and an explanation; the score is empty when there are no votes at all
     * @throws IllegalArgumentException if a number of votes is negative
     */
    public static CombinedRating calculate(double tmdbAverage, int tmdbVotes, double appAverage, int appVotes) {
        if (tmdbVotes < 0 || appVotes < 0) {
            throw new IllegalArgumentException("The number of votes cannot be negative");
        }

        int totalVotes = tmdbVotes + appVotes;
        if (totalVotes == 0) {
            return new CombinedRating(Optional.empty(), 0,
                    "Sem votos: não há informação suficiente para calcular uma nota.");
        }

        if (tmdbAverage < 0 || tmdbAverage > 10 || appAverage < 0 || appAverage > 10) {
            throw new IllegalArgumentException("Averages must be between 0 and 10");
        }

        double sumOfRatings = tmdbAverage * tmdbVotes + appAverage * appVotes;
        double score = (sumOfRatings + NEUTRAL_WEIGHT * NEUTRAL_RATING) / (totalVotes + NEUTRAL_WEIGHT);

        String votesWord = totalVotes == 1 ? "voto" : "votos";
        String explanation = "Baseada em " + totalVotes + " " + votesWord + " (TMDB: " + tmdbVotes
                + ", utilizadores: " + appVotes + ").";
        return new CombinedRating(Optional.of(score), totalVotes, explanation);
    }
}