package com.roslate.backend.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PlaylistTest {

    private final AppUser ana = new AppUser("ana");

    @Test
    void constructorSetsAllFields() {
        Playlist playlist = new Playlist("pl-01", "Ficção científica", ana, false);

        assertThat(playlist.getExternalId()).isEqualTo("pl-01");
        assertThat(playlist.getName()).isEqualTo("Ficção científica");
        assertThat(playlist.getOwner()).isSameAs(ana);
        assertThat(playlist.isDeleted()).isFalse();
    }

    @Test
    void externalIdCanBeNullForPlaylistsCreatedInTheApp() {
        Playlist playlist = new Playlist(null, "My list", ana, false);

        assertThat(playlist.getExternalId()).isNull();
    }

    @Test
    void canBeRenamed() {
        Playlist playlist = new Playlist("pl-01", "Old name", ana, false);

        playlist.setName("New name");

        assertThat(playlist.getName()).isEqualTo("New name");
    }

    @Test
    void canBeMarkedAsDeletedWithoutLosingItsOtherData() {
        Playlist playlist = new Playlist("pl-01", "Ficção científica", ana, false);

        playlist.setDeleted(true);

        assertThat(playlist.isDeleted()).isTrue();
        assertThat(playlist.getName()).isEqualTo("Ficção científica");
        assertThat(playlist.getOwner()).isSameAs(ana);
    }
}
