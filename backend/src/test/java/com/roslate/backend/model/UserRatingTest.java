package com.roslate.backend.model;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class UserRatingTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private final AppUser ana = new AppUser("ana");
    private final LocalDate date = LocalDate.of(2026, 9, 1);

    @Test
    void constructorSetsAllFields() {
        UserRating rating = new UserRating(ana, 27205L, 9, date);

        assertThat(rating.getUser()).isSameAs(ana);
        assertThat(rating.getTmdbId()).isEqualTo(27205L);
        assertThat(rating.getStars()).isEqualTo(9);
        assertThat(rating.getRatedAt()).isEqualTo(date);
    }

    @Test
    void starsAndDateCanBeChangedWhenTheUserChangesTheirRating() {
        UserRating rating = new UserRating(ana, 27205L, 9, date);

        rating.setStars(6);
        rating.setRatedAt(LocalDate.of(2026, 9, 5));

        assertThat(rating.getStars()).isEqualTo(6);
        assertThat(rating.getRatedAt()).isEqualTo(LocalDate.of(2026, 9, 5));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 5, 10})
    void starsBetweenOneAndTenAreValid(int stars) {
        UserRating rating = new UserRating(ana, 27205L, stars, date);

        assertThat(validator.validate(rating)).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0, 11, 100})
    void starsOutsideOneToTenAreRejected(int stars) {
        UserRating rating = new UserRating(ana, 27205L, stars, date);

        assertThat(validator.validate(rating)).hasSize(1);
    }
}
