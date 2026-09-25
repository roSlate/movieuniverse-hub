package com.roslate.backend.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;

/**
 * Turns failures of the TMDB calls into clear answers for the frontend, instead of a generic server error.
 * The answers follow the standard "problem detail" format, with the message in the {@code detail} field.
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(HttpClientErrorException.NotFound.class)
    public ProblemDetail handleNotFound() {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, "Film not found");
    }

    @ExceptionHandler(RestClientResponseException.class)
    public ProblemDetail handleTmdbError(RestClientResponseException exception) {
        log.warn("TMDB answered with status {}", exception.getStatusCode());
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_GATEWAY,
                "TMDB could not answer the request (status " + exception.getStatusCode().value() + ")");
    }

    @ExceptionHandler(ResourceAccessException.class)
    public ProblemDetail handleTmdbUnreachable() {
        log.warn("TMDB could not be reached");
        return ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE,
                "TMDB could not be reached, please try again later");
    }
}