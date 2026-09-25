package com.roslate.backend.seed;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.List;


/**
 * Java mirror of {@code data/seed_playlists.json}, used to read the file.
 * Field names match the JSON keys; {@code versao} and {@code descricao} are ignored.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record SeedData(List<SeedUser> utilizadores, List<SeedPlaylist> playlists, List<SeedRating> notas) {

    /** A user from the seed. */
    public record SeedUser(String nome) {
    }

    /** A playlist from the seed, possibly marked as deleted ({@code apagada}). */
    public record SeedPlaylist(String id, String nome, String utilizador, boolean apagada, List<SeedFilm> filmes) {
    }

    /** A film in a playlist, identified by its TMDB id, with its position ({@code ordem}). */
    public record SeedFilm(@JsonProperty("tmdb_id") Long tmdbId, int ordem) {
    }

    /** A user's rating of a film, from 1 to 10 stars ({@code estrelas}). */
    public record SeedRating(String utilizador, @JsonProperty("tmdb_id") Long tmdbId, int estrelas, LocalDate data) {
    }
}