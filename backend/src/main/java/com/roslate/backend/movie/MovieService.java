package com.roslate.backend.movie;

import com.roslate.backend.model.UserRating;
import com.roslate.backend.rating.CombinedRating;
import com.roslate.backend.rating.CombinedRatingCalculator;
import com.roslate.backend.repository.UserRatingRepository;
import com.roslate.backend.tmdb.TmdbClient;
import com.roslate.backend.tmdb.TmdbMovieDetails;
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
    private final UserRatingRepository userRatings;

    public MovieService(TmdbClient tmdbClient, UserRatingRepository userRatings) {
        this.tmdbClient = tmdbClient;
        this.userRatings = userRatings;
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

    /**
     * Gets everything shown on a film's page: TMDB's details, the ratings of the app's users and the combined rating.
     *
     * @param tmdbId the film's TMDB id
     * @return the film's page data
     */
    public MovieDetail getDetail(long tmdbId) {
        TmdbMovieDetails film = tmdbClient.getMovie(tmdbId);

        List<UserRating> ratings = userRatings.findByTmdbId(tmdbId);
        int appVotes = ratings.size();
        double appAverage = ratings.stream().mapToInt(UserRating::getStars).average().orElse(0.0);

        CombinedRating combined = CombinedRatingCalculator.calculate(
                film.voteAverage(), film.voteCount(), appAverage, appVotes);

        List<String> genres = film.genres().stream().map(TmdbMovieDetails.Genre::name).toList();
        return new MovieDetail(film.id(), film.title(), film.overview(), genres,
                runtimeOf(film.runtime()), yearOf(film.releaseDate()), posterUrlOf(film.posterPath()),
                film.voteAverage(), film.voteCount(), appAverage, appVotes,
                combined.score().orElse(null), combined.totalVotes(), combined.explanation());
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

    private Integer runtimeOf(Integer runtime) {
        if (runtime == null || runtime == 0) {
            return null;
        }
        return runtime;
    }

    private String posterUrlOf(String posterPath) {
        if (posterPath == null) {
            return null;
        }
        return POSTER_BASE_URL + posterPath;
    }
}