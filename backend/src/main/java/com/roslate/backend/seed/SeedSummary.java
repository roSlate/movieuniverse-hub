package com.roslate.backend.seed;

/**
 * How many rows an import actually created. Importing the same seed a second time must give all zeros.
 */
public record SeedSummary(int usersAdded, int playlistsAdded, int filmsAdded, int ratingsAdded) {
}