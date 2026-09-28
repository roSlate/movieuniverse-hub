package com.roslate.backend.rating;

import com.roslate.backend.model.AppUser;
import com.roslate.backend.model.UserRating;
import com.roslate.backend.repository.AppUserRepository;
import com.roslate.backend.repository.UserRatingRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import(RatingService.class)
class RatingServiceTest {

    @Autowired
    private RatingService service;

    @Autowired
    private AppUserRepository users;

    @Autowired
    private UserRatingRepository ratings;

    @Test
    void ratingAFilmCreatesTheUserIfTheyDoNotExistYet() {
        service.rateFilm("ana", 27205L, 9);

        assertThat(users.findByName("ana")).isPresent();
        UserRating rating = ratings.findByUserAndTmdbId(users.findByName("ana").orElseThrow(), 27205L).orElseThrow();
        assertThat(rating.getStars()).isEqualTo(9);
    }

    @Test
    void ratingTheSameFilmAgainUpdatesTheExistingRatingInsteadOfDuplicating() {
        service.rateFilm("ana", 27205L, 9);

        service.rateFilm("ana", 27205L, 4);

        assertThat(ratings.count()).isEqualTo(1);
        AppUser ana = users.findByName("ana").orElseThrow();
        assertThat(ratings.findByUserAndTmdbId(ana, 27205L).orElseThrow().getStars()).isEqualTo(4);
    }

    @Test
    void starsOutsideOneToTenAreRejected() {
        assertThatThrownBy(() -> service.rateFilm("ana", 27205L, 0))
                .isInstanceOf(ResponseStatusException.class);
        assertThatThrownBy(() -> service.rateFilm("ana", 27205L, 11))
                .isInstanceOf(ResponseStatusException.class);
    }
}