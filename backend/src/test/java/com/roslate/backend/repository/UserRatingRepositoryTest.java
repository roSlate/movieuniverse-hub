package com.roslate.backend.repository;

import com.roslate.backend.model.AppUser;
import com.roslate.backend.model.UserRating;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
class UserRatingRepositoryTest {

    @Autowired
    private UserRatingRepository ratings;

    @Autowired
    private AppUserRepository users;

    @Autowired
    private TestEntityManager entityManager;

    private final LocalDate date = LocalDate.of(2026, 9, 1);
    private AppUser ana;
    private AppUser bruno;

    @BeforeEach
    void createUsers() {
        ana = users.save(new AppUser("ana"));
        bruno = users.save(new AppUser("bruno"));
    }

    @Test
    void aSecondRatingByTheSameUserForTheSameFilmIsRejected() {
        ratings.saveAndFlush(new UserRating(ana, 27205L, 9, date));

        assertThatThrownBy(() -> ratings.saveAndFlush(new UserRating(ana, 27205L, 7, date)))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void differentUsersCanRateTheSameFilm() {
        ratings.saveAndFlush(new UserRating(ana, 27205L, 9, date));
        ratings.saveAndFlush(new UserRating(bruno, 27205L, 5, date));

        assertThat(ratings.count()).isEqualTo(2);
    }

    @Test
    void oneUserCanRateDifferentFilms() {
        ratings.saveAndFlush(new UserRating(ana, 27205L, 9, date));
        ratings.saveAndFlush(new UserRating(ana, 603L, 8, date));

        assertThat(ratings.count()).isEqualTo(2);
    }

    @Test
    void findByUserAndTmdbIdReturnsTheRating() {
        ratings.save(new UserRating(ana, 27205L, 9, date));

        assertThat(ratings.findByUserAndTmdbId(ana, 27205L)).isPresent();
        assertThat(ratings.findByUserAndTmdbId(ana, 27205L).get().getStars()).isEqualTo(9);
    }

    @Test
    void findByUserAndTmdbIdReturnsEmptyForAnotherUserOrAnotherFilm() {
        ratings.save(new UserRating(ana, 27205L, 9, date));

        assertThat(ratings.findByUserAndTmdbId(bruno, 27205L)).isEmpty();
        assertThat(ratings.findByUserAndTmdbId(ana, 603L)).isEmpty();
    }

    @Test
    void changingARatingUpdatesTheExistingRowInsteadOfAddingOne() {
        ratings.save(new UserRating(ana, 27205L, 9, date));

        UserRating existing = ratings.findByUserAndTmdbId(ana, 27205L).get();
        existing.setStars(6);
        existing.setRatedAt(LocalDate.of(2026, 9, 5));
        ratings.saveAndFlush(existing);
        entityManager.clear();

        assertThat(ratings.count()).isEqualTo(1);
        UserRating reloaded = ratings.findByUserAndTmdbId(ana, 27205L).get();
        assertThat(reloaded.getStars()).isEqualTo(6);
        assertThat(reloaded.getRatedAt()).isEqualTo(LocalDate.of(2026, 9, 5));
    }

    @Test
    void starsOutsideOneToTenAreRejectedWhenSaving() {
        assertThatThrownBy(() -> ratings.saveAndFlush(new UserRating(ana, 27205L, 11, date)))
                .isInstanceOf(ConstraintViolationException.class);
        assertThatThrownBy(() -> ratings.saveAndFlush(new UserRating(ana, 603L, 0, date)))
                .isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    void findByTmdbIdReturnsTheRatingsOfEveryUserForThatFilm() {
        ratings.save(new UserRating(ana, 27205L, 9, date));
        ratings.save(new UserRating(bruno, 27205L, 5, date));
        ratings.save(new UserRating(ana, 603L, 8, date));

        assertThat(ratings.findByTmdbId(27205L)).extracting(UserRating::getStars)
                .containsExactlyInAnyOrder(9, 5);
    }

    @Test
    void findByTmdbIdReturnsAnEmptyListForAFilmNobodyRated() {
        assertThat(ratings.findByTmdbId(27205L)).isEmpty();
    }
}