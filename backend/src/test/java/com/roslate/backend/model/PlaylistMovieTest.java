package com.roslate.backend.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PlaylistMovieTest {

    private final Playlist playlist = new Playlist("pl-01", "Ficção científica", new AppUser("ana"), false);

    @Test
    void constructorSetsAllFields() {
        PlaylistMovie entry = new PlaylistMovie(playlist, 27205L, 1);

        assertThat(entry.getPlaylist()).isSameAs(playlist);
        assertThat(entry.getTmdbId()).isEqualTo(27205L);
        assertThat(entry.getSortOrder()).isEqualTo(1);
    }

    @Test
    void sortOrderCanBeChanged() {
        PlaylistMovie entry = new PlaylistMovie(playlist, 27205L, 1);

        entry.setSortOrder(3);

        assertThat(entry.getSortOrder()).isEqualTo(3);
    }
}
