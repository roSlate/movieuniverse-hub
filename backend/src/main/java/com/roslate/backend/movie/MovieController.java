package com.roslate.backend.movie;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * The REST endpoints for finding films.
 */
@RestController
@RequestMapping("/api/movies")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    /**
     * Searches films by title.
     *
     * @param title the text to search for; must not be blank
     * @return the matching films
     */
    @GetMapping("/search")
    public List<MovieSearchItem> search(@RequestParam String title) {
        if (title.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The title to search for cannot be empty");
        }
        return movieService.search(title);
    }

    /**
     * Gets everything shown on a film's page, including the combined rating.
     *
     * @param tmdbId the film's TMDB id
     * @return the film's page data
     */
    @GetMapping("/{tmdbId}")
    public MovieDetail detail(@PathVariable long tmdbId) {
        return movieService.getDetail(tmdbId);
    }
}