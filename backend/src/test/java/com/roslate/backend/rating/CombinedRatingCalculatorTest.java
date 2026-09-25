package com.roslate.backend.rating;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class CombinedRatingCalculatorTest {

    private double score(CombinedRating rating) {
        return rating.score().orElseThrow();
    }

    @Test
    void aHighAverageWithFewVotesRanksBelowASolidAverageWithManyVotes() {
        CombinedRating fewVotes = CombinedRatingCalculator.calculate(8.9, 12, 0, 0);
        CombinedRating manyVotes = CombinedRatingCalculator.calculate(8.4, 30000, 0, 0);

        assertThat(score(fewVotes)).isLessThan(score(manyVotes));
    }

    @Test
    void threeUsersGivingTenDoNotChangeThatOrder() {
        CombinedRating fewVotes = CombinedRatingCalculator.calculate(8.9, 12, 0, 0);
        CombinedRating fewVotesPlusUsers = CombinedRatingCalculator.calculate(8.9, 12, 10, 3);
        CombinedRating manyVotes = CombinedRatingCalculator.calculate(8.4, 30000, 0, 0);

        assertThat(score(fewVotesPlusUsers)).isGreaterThan(score(fewVotes));
        assertThat(score(fewVotesPlusUsers)).isLessThan(score(manyVotes));
    }

    @Test
    void noVotesAtAllMeansNoScore() {
        CombinedRating rating = CombinedRatingCalculator.calculate(0.0, 0, 0.0, 0);

        assertThat(rating.score()).isEmpty();
        assertThat(rating.totalVotes()).isZero();
    }

    @Test
    void theExplanationSaysThereIsNotEnoughInformationWhenThereAreNoVotes() {
        CombinedRating rating = CombinedRatingCalculator.calculate(0.0, 0, 0.0, 0);

        assertThat(rating.explanation()).contains("não há informação suficiente");
    }

    @Test
    void anAverageWithoutVotesBehindItIsNotTurnedIntoAScore() {
        CombinedRating rating = CombinedRatingCalculator.calculate(7.5, 0, 9.0, 0);

        assertThat(rating.score()).isEmpty();
    }

    @Test
    void ratingsFromOnlyTheAppUsersStillGiveAScore() {
        CombinedRating rating = CombinedRatingCalculator.calculate(0.0, 0, 9.0, 2);

        assertThat(rating.totalVotes()).isEqualTo(2);
        assertThat(score(rating)).isBetween(CombinedRatingCalculator.NEUTRAL_RATING, 9.0);
    }

    @Test
    void aSourceWithoutVotesDoesNotAffectTheScore() {
        CombinedRating withZeroAverage = CombinedRatingCalculator.calculate(8.4, 30000, 0.0, 0);
        CombinedRating withOtherAverage = CombinedRatingCalculator.calculate(8.4, 30000, 3.0, 0);

        assertThat(score(withZeroAverage)).isEqualTo(score(withOtherAverage));
    }

    @Test
    void votesFromBothSourcesAreAddedTogetherAndExplained() {
        CombinedRating rating = CombinedRatingCalculator.calculate(8.0, 100, 6.0, 50);

        assertThat(rating.totalVotes()).isEqualTo(150);
        assertThat(rating.explanation()).contains("150", "TMDB: 100", "utilizadores: 50");
    }

    @Test
    void theScoreAlwaysStaysBetweenZeroAndTen() {
        CombinedRating highest = CombinedRatingCalculator.calculate(10.0, 1_000_000, 10.0, 1_000_000);
        CombinedRating lowest = CombinedRatingCalculator.calculate(0.0, 1_000_000, 0.0, 1);

        assertThat(score(highest)).isBetween(0.0, 10.0);
        assertThat(score(lowest)).isBetween(0.0, 10.0);
    }

    @Test
    void aNegativeNumberOfVotesIsRejected() {
        assertThatThrownBy(() -> CombinedRatingCalculator.calculate(8.0, -1, 0.0, 0))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> CombinedRatingCalculator.calculate(0.0, 0, 8.0, -1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void anAverageOutsideZeroToTenIsRejectedWhenThereAreVotes() {
        assertThatThrownBy(() -> CombinedRatingCalculator.calculate(10.5, 100, 0.0, 0))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> CombinedRatingCalculator.calculate(8.0, 100, -1.0, 5))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void anOutOfRangeAverageIsNotCheckedWhenThereAreNoVotesAtAll() {
        CombinedRating rating = CombinedRatingCalculator.calculate(15.0, 0, 0.0, 0);

        assertThat(rating.score()).isEmpty();
    }

    @Test
    void theExplanationUsesTheSingularForASingleVote() {
        CombinedRating rating = CombinedRatingCalculator.calculate(0.0, 0, 8.0, 1);

        assertThat(rating.explanation()).contains("1 voto ").doesNotContain("votos");
    }
}