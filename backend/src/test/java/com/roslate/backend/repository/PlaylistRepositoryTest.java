package com.roslate.backend.repository;

import com.roslate.backend.model.AppUser;
import com.roslate.backend.model.Playlist;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
class PlaylistRepositoryTest {

    @Autowired
    private PlaylistRepository playlists;

    @Autowired
    private AppUserRepository users;

    private AppUser ana;

    @BeforeEach
    void createOwner() {
        ana = users.save(new AppUser("ana"));
    }

    @Test
    void findByExternalIdReturnsThePlaylist() {
        playlists.save(new Playlist("pl-01", "Ficção científica", ana, false));

        assertThat(playlists.findByExternalId("pl-01")).isPresent();
        assertThat(playlists.findByExternalId("pl-01").get().getName()).isEqualTo("Ficção científica");
    }

    @Test
    void findByExternalIdReturnsEmptyForAnUnknownId() {
        playlists.save(new Playlist("pl-01", "Ficção científica", ana, false));

        assertThat(playlists.findByExternalId("pl-99")).isEmpty();
    }

    @Test
    void twoPlaylistsWithTheSameExternalIdAreRejected() {
        playlists.saveAndFlush(new Playlist("pl-01", "First", ana, false));

        assertThatThrownBy(() -> playlists.saveAndFlush(new Playlist("pl-01", "Second", ana,
                false)))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void severalPlaylistsWithoutExternalIdAreAllowed() {
        playlists.saveAndFlush(new Playlist(null, "Mine 1", ana, false));
        playlists.saveAndFlush(new Playlist(null, "Mine 2", ana, false));

        assertThat(playlists.count()).isEqualTo(2);
    }

    @Test
    void aPlaylistWithoutAnOwnerIsRejected() {
        assertThatThrownBy(() -> playlists.saveAndFlush(new Playlist("pl-01", "Orphan", null,
                false)))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void theDeletedFlagIsStored() {
        playlists.save(new Playlist("pl-03", "Rascunho antigo", ana, true));

        assertThat(playlists.findByExternalId("pl-03").get().isDeleted()).isTrue();
    }
}