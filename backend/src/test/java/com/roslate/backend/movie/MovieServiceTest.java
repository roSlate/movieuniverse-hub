package com.roslate.backend.movie;

import com.roslate.backend.tmdb.TmdbClient;
import com.roslate.backend.tmdb.TmdbMovieSummary;
import com.roslate.backend.tmdb.TmdbSearchResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.roslate.backend.repository.UserRatingRepository;

import com.roslate.backend.model.AppUser;
import com.roslate.backend.model.UserRating;
import com.roslate.backend.rating.CombinedRatingCalculator;
import com.roslate.backend.tmdb.TmdbMovieDetails;

import java.time.LocalDate;

class MovieServiceTest {

    private TmdbClient tmdbClient;
    private MovieService service;
    private UserRatingRepository userRatings;

    @BeforeEach
    void setUp() {
        tmdbClient = mock(TmdbClient.class);
        userRatings = mock(UserRatingRepository.class);
        service = new MovieService(tmdbClient, userRatings);
    }

    private void tmdbReturns(TmdbMovieSummary... films) {
        when(tmdbClient.searchMovies("origem"))
                .thenReturn(new TmdbSearchResponse(1, List.of(films), 1, films.length));
    }

    @Test
    void eachTmdbFilmBecomesASearchItem() {
        tmdbReturns(new TmdbMovieSummary(27205L, "A Origem", "2010-07-15", "/abc.jpg",
                "Sonhos.", 8.4, 37000));

        List<MovieSearchItem> items = service.search("origem");

        assertThat(items).containsExactly(new MovieSearchItem(27205L, "A Origem", 2010,
                "https://image.tmdb.org/t/p/w500/abc.jpg", 8.4, 37000));
    }

    @Test
    void theOrderOfTheResultsIsKept() {
        tmdbReturns(new TmdbMovieSummary(1L, "Primeiro", "2001-01-01", null, "",
                        5.0, 10),
                new TmdbMovieSummary(2L, "Segundo", "2002-02-02", null, "",
                        6.0, 20));

        assertThat(service.search("origem")).extracting(MovieSearchItem::title)
                .containsExactly("Primeiro", "Segundo");
    }

    @Test
    void anUnknownReleaseDateGivesNoYear() {
        tmdbReturns(new TmdbMovieSummary(1L, "Sem data", "", "/p.jpg", "",
                0.0, 0));

        assertThat(service.search("origem").get(0).year()).isNull();
    }

    @Test
    void aFilmWithoutAPosterGivesNoPosterUrl() {
        tmdbReturns(new TmdbMovieSummary(1L, "Sem poster", "2001-01-01", null,
                "", 0.0, 0));

        assertThat(service.search("origem").get(0).posterUrl()).isNull();
    }

    @Test
    void aFilmWithoutVotesKeepsAVoteCountOfZero() {
        tmdbReturns(new TmdbMovieSummary(1L, "Sem votos", "2001-01-01", "/p.jpg",
                "", 0.0, 0));

        assertThat(service.search("origem").get(0).voteCount()).isZero();
    }

    @Test
    void noMatchesGivesAnEmptyList() {
        tmdbReturns();

        assertThat(service.search("origem")).isEmpty();
    }

    private void tmdbHasFilm(Integer runtime, String releaseDate, String posterPath, double average, int votes) {
        when(tmdbClient.getMovie(27205L)).thenReturn(new TmdbMovieDetails(27205L, "A Origem", "Sonhos.",
                List.of(new TmdbMovieDetails.Genre(28, "Ação"), new TmdbMovieDetails.Genre(878, "Ficção científica")),
                runtime, releaseDate, posterPath, average, votes));
    }

    private UserRating rating(String user, int stars) {
        return new UserRating(new AppUser(user), 27205L, stars, LocalDate.of(2026, 9, 1));
    }

    @Test
    void theDetailCarriesTheFilmDescription() {
        tmdbHasFilm(148, "2010-07-15", "/abc.jpg", 8.4, 37000);

        MovieDetail detail = service.getDetail(27205L);

        assertThat(detail.tmdbId()).isEqualTo(27205L);
        assertThat(detail.title()).isEqualTo("A Origem");
        assertThat(detail.overview()).isEqualTo("Sonhos.");
        assertThat(detail.genres()).containsExactly("Ação", "Ficção científica");
        assertThat(detail.runtimeMinutes()).isEqualTo(148);
        assertThat(detail.year()).isEqualTo(2010);
        assertThat(detail.posterUrl()).isEqualTo("https://image.tmdb.org/t/p/w500/abc.jpg");
        assertThat(detail.tmdbAverage()).isEqualTo(8.4);
        assertThat(detail.tmdbVotes()).isEqualTo(37000);
    }

    @Test
    void theCombinedRatingUsesTmdbAndTheAppUsersTogether() {
        tmdbHasFilm(148, "2010-07-15", "/abc.jpg", 8.4, 37000);
        when(userRatings.findByTmdbId(27205L)).thenReturn(List.of(rating("ana", 9), rating("bruno", 7)));

        MovieDetail detail = service.getDetail(27205L);

        assertThat(detail.appVotes()).isEqualTo(2);
        assertThat(detail.appAverage()).isEqualTo(8.0);
        assertThat(detail.combinedVotes()).isEqualTo(37002);
        double expected = CombinedRatingCalculator.calculate(8.4, 37000, 8.0, 2).score().orElseThrow();
        assertThat(detail.combinedScore()).isEqualTo(expected);
    }

    @Test
    void aFilmWithNoVotesAtAllHasNoCombinedScore() {
        tmdbHasFilm(null, "", null, 0.0, 0);

        MovieDetail detail = service.getDetail(27205L);

        assertThat(detail.combinedScore()).isNull();
        assertThat(detail.combinedVotes()).isZero();
        assertThat(detail.combinedExplanation()).contains("não há informação suficiente");
        assertThat(detail.appVotes()).isZero();
    }

    @Test
    void aFilmWithOnlyUserRatingsStillGetsACombinedScore() {
        tmdbHasFilm(120, "2010-07-15", "/abc.jpg", 0.0, 0);
        when(userRatings.findByTmdbId(27205L)).thenReturn(List.of(rating("ana", 10)));

        MovieDetail detail = service.getDetail(27205L);

        assertThat(detail.combinedScore()).isNotNull();
        assertThat(detail.combinedVotes()).isEqualTo(1);
    }

    @Test
    void missingRuntimeYearAndPosterBecomeNull() {
        tmdbHasFilm(0, "", null, 0.0, 0);

        MovieDetail detail = service.getDetail(27205L);

        assertThat(detail.runtimeMinutes()).isNull();
        assertThat(detail.year()).isNull();
        assertThat(detail.posterUrl()).isNull();
    }
}