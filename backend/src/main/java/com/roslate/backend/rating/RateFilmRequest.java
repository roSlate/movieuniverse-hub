package com.roslate.backend.rating;

/**
 * The body of a "rate this film" request.
 */
public record RateFilmRequest(int stars) {
}