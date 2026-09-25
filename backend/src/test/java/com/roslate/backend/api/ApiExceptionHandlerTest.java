package com.roslate.backend.api;

import com.roslate.backend.movie.MovieController;
import com.roslate.backend.movie.MovieService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.nio.charset.StandardCharsets;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MovieController.class)
class ApiExceptionHandlerTest {

    private static final byte[] NO_BODY = new byte[0];

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MovieService movieService;

    @Test
    void aFilmUnknownToTmdbBecomesA404() throws Exception {
        when(movieService.search("x")).thenThrow(HttpClientErrorException.create(
                HttpStatus.NOT_FOUND, "Not Found", HttpHeaders.EMPTY, NO_BODY, StandardCharsets.UTF_8));

        mockMvc.perform(get("/api/movies/search").param("title", "x"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Film not found"));
    }

    @Test
    void aRejectedCredentialBecomesA502WithoutExposingDetails() throws Exception {
        when(movieService.search("x")).thenThrow(HttpClientErrorException.create(
                HttpStatus.UNAUTHORIZED, "Unauthorized", HttpHeaders.EMPTY, NO_BODY, StandardCharsets.UTF_8));

        mockMvc.perform(get("/api/movies/search").param("title", "x"))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.detail").value("TMDB could not answer the request " +
                        "(status 401)"));
    }

    @Test
    void aTmdbServerErrorBecomesA502() throws Exception {
        when(movieService.search("x")).thenThrow(HttpServerErrorException.create(
                HttpStatus.INTERNAL_SERVER_ERROR, "Error", HttpHeaders.EMPTY, NO_BODY, StandardCharsets.UTF_8));

        mockMvc.perform(get("/api/movies/search").param("title", "x"))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.detail").value("TMDB could not answer the request " +
                        "(status 500)"));
    }

    @Test
    void anUnreachableTmdbBecomesA503() throws Exception {
        when(movieService.search("x")).thenThrow(new ResourceAccessException("timeout"));

        mockMvc.perform(get("/api/movies/search").param("title", "x"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.detail").value("TMDB could not be reached, please try " +
                        "again later"));
    }

    @Test
    void aFilmMissingFromTmdbBecomesA404OnTheDetailsEndpointToo() throws Exception {
        when(movieService.getDetail(999999L)).thenThrow(HttpClientErrorException.create(
                HttpStatus.NOT_FOUND, "Not Found", HttpHeaders.EMPTY, NO_BODY, StandardCharsets.UTF_8));

        mockMvc.perform(get("/api/movies/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Film not found"));
    }
}