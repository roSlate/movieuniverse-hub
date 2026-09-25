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

class MovieServiceTest {

    private TmdbClient tmdbClient;
    private MovieService service;

    @BeforeEach
    void setUp() {
        tmdbClient = mock(TmdbClient.class);
        service = new MovieService(tmdbClient);
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
}