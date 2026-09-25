package com.roslate.backend.movie;

import com.roslate.backend.tmdb.TmdbClient;
import com.roslate.backend.tmdb.TmdbMovieSummary;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * Finds films through TMDB and translates them into the shapes shown to the user.
 */
@Service
public class MovieService {

    private static final String POSTER_BASE_URL = "https://image.tmdb.org/t/p/w500";

    private final TmdbClient tmdbClient;

    public MovieService(TmdbClient tmdbClient) {
        this.tmdbClient = tmdbClient;
    }

    /**
     * Searches films by title.
     *
     * @param title the text to search for
     * @return the matching films, in the order TMDB returned them; empty when nothing matches
     */
    public List<MovieSearchItem> search(String title) {
        return tmdbClient.searchMovies(title).results().stream()
                .map(this::toSearchItem)
                .toList();
    }

    private MovieSearchItem toSearchItem(TmdbMovieSummary film) {
        return new MovieSearchItem(film.id(), film.title(), yearOf(film.releaseDate()),
                posterUrlOf(film.posterPath()), film.voteAverage(), film.voteCount());
    }

    private Integer yearOf(String releaseDate) {
        if (releaseDate == null || releaseDate.isBlank()) {
            return null;
        }
        return LocalDate.parse(releaseDate).getYear();
    }

    private String posterUrlOf(String posterPath) {
        if (posterPath == null) {
            return null;
        }
        return POSTER_BASE_URL + posterPath;
    }
}