package com.roslate.backend.repository;

import com.roslate.backend.model.AppUser;
import com.roslate.backend.model.Playlist;
import com.roslate.backend.model.PlaylistMovie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
class PlaylistMovieRepositoryTest {

    @Autowired
    private PlaylistMovieRepository entries;

    @Autowired
    private PlaylistRepository playlists;

    @Autowired
    private AppUserRepository users;

    private Playlist scifi;
    private Playlist favourites;

    @BeforeEach
    void createPlaylists() {
        AppUser ana = users.save(new AppUser("ana"));
        scifi = playlists.save(new Playlist("pl-01", "Ficção científica", ana, false));
        favourites = playlists.save(new Playlist("pl-02", "Favoritos", ana, false));
    }

    @Test
    void theSameFilmTwiceInOnePlaylistIsRejected() {
        entries.saveAndFlush(new PlaylistMovie(scifi, 27205L, 1));

        assertThatThrownBy(() -> entries.saveAndFlush(new PlaylistMovie(scifi, 27205L, 2)))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void theSameFilmInTwoDifferentPlaylistsIsAllowed() {
        entries.saveAndFlush(new PlaylistMovie(scifi, 27205L, 1));
        entries.saveAndFlush(new PlaylistMovie(favourites, 27205L, 1));

        assertThat(entries.count()).isEqualTo(2);
    }

    @Test
    void differentFilmsInOnePlaylistAreAllowed() {
        entries.saveAndFlush(new PlaylistMovie(scifi, 27205L, 1));
        entries.saveAndFlush(new PlaylistMovie(scifi, 603L, 2));

        assertThat(entries.count()).isEqualTo(2);
    }

    @Test
    void existsByPlaylistAndTmdbIdIsTrueForAFilmInThePlaylist() {
        entries.save(new PlaylistMovie(scifi, 27205L, 1));

        assertThat(entries.existsByPlaylistAndTmdbId(scifi, 27205L)).isTrue();
    }

    @Test
    void existsByPlaylistAndTmdbIdIsFalseForAFilmNotInThePlaylist() {
        entries.save(new PlaylistMovie(scifi, 27205L, 1));

        assertThat(entries.existsByPlaylistAndTmdbId(scifi, 603L)).isFalse();
    }

    @Test
    void existsByPlaylistAndTmdbIdIsFalseWhenTheFilmIsOnlyInAnotherPlaylist() {
        entries.save(new PlaylistMovie(scifi, 27205L, 1));

        assertThat(entries.existsByPlaylistAndTmdbId(favourites, 27205L)).isFalse();
    }
}