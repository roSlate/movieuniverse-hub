package com.roslate.backend.tmdb;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class TmdbClientTest {

    private static final String BASE_URL = "https://api.themoviedb.org/3";

    private MockRestServiceServer server;
    private TmdbClient client;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = TmdbConfig.restClientBuilder(BASE_URL, "test-token");
        server = MockRestServiceServer.bindTo(builder).build();
        client = new TmdbClient(builder.build());
    }

    @Test
    void searchAsksTmdbForTheTitleAndReadsTheResults() {
        String json = """
                {"page": 1, "total_pages": 1, "total_results": 1,
                 "results": [{"id": 27205, "title": "A Origem", "release_date": "2010-07-15",
                              "poster_path": "/abc.jpg", "overview": "Sonhos.", "vote_average": 8.4,
                              "vote_count": 37000, "popularity": 99.9, "adult": false}]}
                """;
        server.expect(requestTo(BASE_URL + "/search/movie?query=A%20Origem&language=pt-PT"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        TmdbSearchResponse response = client.searchMovies("A Origem");

        assertThat(response.totalResults()).isEqualTo(1);
        TmdbMovieSummary film = response.results().get(0);
        assertThat(film.id()).isEqualTo(27205L);
        assertThat(film.title()).isEqualTo("A Origem");
        assertThat(film.releaseDate()).isEqualTo("2010-07-15");
        assertThat(film.voteAverage()).isEqualTo(8.4);
        assertThat(film.voteCount()).isEqualTo(37000);
        server.verify();
    }

    @Test
    void detailsReadGenresRuntimeAndVotes() {
        String json = """
                {"id": 27205, "title": "A Origem", "overview": "Sonhos.", "runtime": 148,
                 "genres": [{"id": 28, "name": "Ação"}, {"id": 878, "name": "Ficção científica"}],
                 "release_date": "2010-07-15", "poster_path": "/abc.jpg",
                 "vote_average": 8.4, "vote_count": 37000, "budget": 160000000}
                """;
        server.expect(requestTo(BASE_URL + "/movie/27205?language=pt-PT"))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        TmdbMovieDetails film = client.getMovie(27205);

        assertThat(film.title()).isEqualTo("A Origem");
        assertThat(film.runtime()).isEqualTo(148);
        assertThat(film.genres()).extracting(TmdbMovieDetails.Genre::name)
                .containsExactly("Ação", "Ficção científica");
        assertThat(film.voteCount()).isEqualTo(37000);
    }

    @Test
    void detailsCopeWithAMissingPosterAndRuntime() {
        String json = """
                {"id": 1, "title": "Sem dados", "overview": "", "runtime": null, "genres": [],
                 "release_date": "", "poster_path": null, "vote_average": 0.0, "vote_count": 0}
                """;
        server.expect(requestTo(BASE_URL + "/movie/1?language=pt-PT"))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        TmdbMovieDetails film = client.getMovie(1);

        assertThat(film.posterPath()).isNull();
        assertThat(film.runtime()).isNull();
        assertThat(film.voteCount()).isZero();
    }

    @Test
    void searchWithNoMatchesGivesAnEmptyList() {
        server.expect(requestTo(BASE_URL + "/search/movie?query=xyzxyz&language=pt-PT"))
                .andRespond(withSuccess("{\"page\": 1, \"results\": [], \"total_pages\": 0, \"total_results\": 0}",
                        MediaType.APPLICATION_JSON));

        assertThat(client.searchMovies("xyzxyz").results()).isEmpty();
    }

    @Test
    void everyRequestCarriesTheTokenInTheAuthorizationHeader() {
        server.expect(requestTo(BASE_URL + "/movie/27205?language=pt-PT"))
                .andExpect(header("Authorization", "Bearer test-token"))
                .andRespond(withSuccess(
                        "{\"id\": 27205, \"title\": \"A Origem\", \"vote_average\": 8.4, \"vote_count\": 37000}",
                        MediaType.APPLICATION_JSON));

        client.getMovie(27205);

        server.verify();
    }

    @Test
    void aRejectedCredentialSurfacesAsAnError() {
        server.expect(requestTo(BASE_URL + "/movie/27205?language=pt-PT"))
                .andRespond(withStatus(HttpStatus.UNAUTHORIZED));

        assertThatThrownBy(() -> client.getMovie(27205)).isInstanceOf(HttpClientErrorException.class);
    }
}