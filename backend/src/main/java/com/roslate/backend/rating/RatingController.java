package com.roslate.backend.rating;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST endpoint for a user to rate (or re-rate) a film.
 */
@RestController
public class RatingController {

    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @PutMapping("/api/users/{name}/ratings/{tmdbId}")
    public void rateFilm(@PathVariable String name, @PathVariable long tmdbId, @RequestBody RateFilmRequest request) {
        ratingService.rateFilm(name, tmdbId, request.stars());
    }
}