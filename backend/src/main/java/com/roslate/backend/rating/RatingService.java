package com.roslate.backend.rating;

import com.roslate.backend.model.AppUser;
import com.roslate.backend.model.UserRating;
import com.roslate.backend.repository.AppUserRepository;
import com.roslate.backend.repository.UserRatingRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

/**
 * Lets a user rate a film 1-10, or change their existing rating. Users are identified only by name.
 */
@Service
public class RatingService {

    private final AppUserRepository users;
    private final UserRatingRepository ratings;

    public RatingService(AppUserRepository users, UserRatingRepository ratings) {
        this.users = users;
        this.ratings = ratings;
    }

    public void rateFilm(String userName, long tmdbId, int stars) {
        if (stars < 1 || stars > 10) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Stars must be between 1 and 10");
        }
        AppUser user = users.findByName(userName).orElseGet(() -> users.save(new AppUser(userName)));
        UserRating existing = ratings.findByUserAndTmdbId(user, tmdbId).orElse(null);
        if (existing == null) {
            ratings.save(new UserRating(user, tmdbId, stars, LocalDate.now()));
        } else {
            existing.setStars(stars);
            existing.setRatedAt(LocalDate.now());
            ratings.save(existing);
        }
    }
}