package com.roslate.backend.movie;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.hamcrest.Matchers.containsString;

@WebMvcTest(MovieController.class)
class MovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MovieService movieService;

    @Test
    void searchReturnsTheFilmsAsJson() throws Exception {
        when(movieService.search("origem")).thenReturn(List.of(
                new MovieSearchItem(27205L, "A Origem", 2010,
                        "https://image.tmdb.org/t/p/w500/abc.jpg", 8.4, 37000)));

        mockMvc.perform(get("/api/movies/search").param("title", "origem"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].tmdbId").value(27205))
                .andExpect(jsonPath("$[0].title").value("A Origem"))
                .andExpect(jsonPath("$[0].year").value(2010))
                .andExpect(jsonPath("$[0].posterUrl").value("https://image.tmdb.org/t/p/w500/abc.jpg"))
                .andExpect(jsonPath("$[0].voteAverage").value(8.4))
                .andExpect(jsonPath("$[0].voteCount").value(37000));
    }

    @Test
    void noMatchesGivesAnEmptyJsonList() throws Exception {
        when(movieService.search("xyz")).thenReturn(List.of());

        mockMvc.perform(get("/api/movies/search").param("title", "xyz"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void aMissingYearAndPosterAppearAsNullInTheJson() throws Exception {
        when(movieService.search("origem")).thenReturn(List.of(
                new MovieSearchItem(1L, "Sem dados", null, null, 0.0, 0)));

        mockMvc.perform(get("/api/movies/search").param("title", "origem"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].year").value(nullValue()))
                .andExpect(jsonPath("$[0].posterUrl").value(nullValue()));
    }

    @Test
    void aBlankTitleIsRejectedWithoutCallingTheService() throws Exception {
        mockMvc.perform(get("/api/movies/search").param("title", "   "))
                .andExpect(status().isBadRequest());

        verify(movieService, never()).search(any());
    }

    @Test
    void aMissingTitleIsRejected() throws Exception {
        mockMvc.perform(get("/api/movies/search"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void detailReturnsTheFilmPageAsJson() throws Exception {
        when(movieService.getDetail(27205L)).thenReturn(new MovieDetail(27205L, "A Origem",
                "Sonhos.",
                List.of("Ação", "Ficção científica"), 148, 2010,
                "https://image.tmdb.org/t/p/w500/abc.jpg",
                8.4, 37000, 8.0, 2, 8.39, 37002,
                "Baseada em 37002 votos (TMDB: 37000, utilizadores: 2)."));

        mockMvc.perform(get("/api/movies/27205"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tmdbId").value(27205))
                .andExpect(jsonPath("$.title").value("A Origem"))
                .andExpect(jsonPath("$.genres.length()").value(2))
                .andExpect(jsonPath("$.genres[0]").value("Ação"))
                .andExpect(jsonPath("$.runtimeMinutes").value(148))
                .andExpect(jsonPath("$.tmdbAverage").value(8.4))
                .andExpect(jsonPath("$.tmdbVotes").value(37000))
                .andExpect(jsonPath("$.appAverage").value(8.0))
                .andExpect(jsonPath("$.appVotes").value(2))
                .andExpect(jsonPath("$.combinedScore").value(8.39))
                .andExpect(jsonPath("$.combinedVotes").value(37002))
                .andExpect(jsonPath("$.combinedExplanation")
                        .value("Baseada em 37002 votos (TMDB: 37000, utilizadores: 2)."));
    }

    @Test
    void aFilmWithoutACombinedScoreShowsNullAndTheExplanation() throws Exception {
        when(movieService.getDetail(1L)).thenReturn(new MovieDetail(1L, "Sem votos", "", List.of(), null, null, null,
                0.0, 0, 0.0, 0, null, 0, "Sem votos: não há informação suficiente para calcular uma nota."));

        mockMvc.perform(get("/api/movies/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.combinedScore").value(nullValue()))
                .andExpect(jsonPath("$.combinedExplanation").value(containsString("não há informação suficiente")));
    }

    @Test
    void aFilmIdThatIsNotANumberIsRejected() throws Exception {
        mockMvc.perform(get("/api/movies/abc"))
                .andExpect(status().isBadRequest());
    }
}