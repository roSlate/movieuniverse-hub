package com.roslate.backend.playlist;

import com.roslate.backend.model.AppUser;
import com.roslate.backend.model.Playlist;
import com.roslate.backend.repository.AppUserRepository;
import com.roslate.backend.repository.PlaylistMovieRepository;
import com.roslate.backend.repository.PlaylistRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import(PlaylistService.class)
class PlaylistServiceTest {

    @Autowired
    private PlaylistService service;

    @Autowired
    private AppUserRepository users;

    @Autowired
    private PlaylistRepository playlists;

    @Autowired
    private PlaylistMovieRepository entries;

    @BeforeEach
    void setUp() {
        users.save(new AppUser("ana"));
    }

    @Test
    void creatingAPlaylistCreatesTheUserIfTheyDoNotExistYet() {
        PlaylistSummary summary = service.createPlaylist("bruno", "Novidades");

        assertThat(summary.name()).isEqualTo("Novidades");
        assertThat(users.findByName("bruno")).isPresent();
    }

    @Test
    void creatingAPlaylistForAnExistingUserDoesNotCreateAnotherOne() {
        service.createPlaylist("ana", "Sci-fi");

        assertThat(users.count()).isEqualTo(1);
    }

    @Test
    void listingPlaylistsOnlyReturnsTheOnesThatAreNotDeleted() {
        AppUser ana = users.findByName("ana").orElseThrow();
        playlists.save(new Playlist(null, "Ativa", ana, false));
        playlists.save(new Playlist(null, "Apagada", ana, true));

        List<PlaylistSummary> result = service.listPlaylists("ana");

        assertThat(result).extracting(PlaylistSummary::name).containsExactly("Ativa");
    }

    @Test
    void addingAFilmThatIsAlreadyInThePlaylistDoesNothing() {
        AppUser ana = users.findByName("ana").orElseThrow();
        Playlist playlist = playlists.save(new Playlist(null, "Sci-fi", ana, false));
        service.addFilm(playlist.getId(), 27205L);

        service.addFilm(playlist.getId(), 27205L);

        assertThat(entries.count()).isEqualTo(1);
    }

    @Test
    void addingAndRemovingAFilmWorksTogether() {
        AppUser ana = users.findByName("ana").orElseThrow();
        Playlist playlist = playlists.save(new Playlist(null, "Sci-fi", ana, false));

        service.addFilm(playlist.getId(), 27205L);
        assertThat(service.getPlaylist(playlist.getId()).tmdbIds()).containsExactly(27205L);

        service.removeFilm(playlist.getId(), 27205L);
        assertThat(service.getPlaylist(playlist.getId()).tmdbIds()).isEmpty();
    }

    @Test
    void removingAFilmThatIsNotInThePlaylistDoesNothing() {
        AppUser ana = users.findByName("ana").orElseThrow();
        Playlist playlist = playlists.save(new Playlist(null, "Sci-fi", ana, false));

        service.removeFilm(playlist.getId(), 27205L);

        assertThat(entries.count()).isZero();
    }

    @Test
    void aRequestForAnUnknownPlaylistIsRejectedWithA404() {
        assertThatThrownBy(() -> service.getPlaylist(99L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Playlist not found");
    }
}