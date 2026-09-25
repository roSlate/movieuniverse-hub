package com.roslate.backend.seed;

import com.roslate.backend.model.Playlist;
import com.roslate.backend.model.PlaylistMovie;
import com.roslate.backend.model.UserRating;
import com.roslate.backend.repository.AppUserRepository;
import com.roslate.backend.repository.PlaylistMovieRepository;
import com.roslate.backend.repository.PlaylistRepository;
import com.roslate.backend.repository.UserRatingRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import tools.jackson.databind.json.JsonMapper;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@DataJpaTest
@Import(SeedImportService.class)
class SeedImportServiceTest {

    @Autowired
    private SeedImportService service;

    @Autowired
    private AppUserRepository users;

    @Autowired
    private PlaylistRepository playlists;

    @Autowired
    private PlaylistMovieRepository entries;

    @Autowired
    private UserRatingRepository ratings;

    private SeedData realSeed() {
        return JsonMapper.builder().build().readValue(Path.of("../data/seed_playlists.json").toFile(), SeedData.class);
    }

    private SeedData smallSeed(List<SeedData.SeedFilm> films) {
        return new SeedData(
                List.of(new SeedData.SeedUser("ana")),
                List.of(new SeedData.SeedPlaylist("pl-01", "Sci-fi", "ana", false, films)),
                List.of(new SeedData.SeedRating("ana", 27205L, 9, LocalDate.of(2026, 9, 1))));
    }

    @Test
    void importsTheProvidedSeedFile() {
        SeedSummary summary = service.importSeed(realSeed());

        assertThat(summary).isEqualTo(new SeedSummary(3, 10, 50, 20));
        assertThat(users.count()).isEqualTo(3);
        assertThat(playlists.count()).isEqualTo(10);
        assertThat(entries.count()).isEqualTo(50);
        assertThat(ratings.count()).isEqualTo(20);
    }

    @Test
    void importingTheSameSeedTwiceDoesNotDuplicateAnything() {
        service.importSeed(realSeed());

        SeedSummary second = service.importSeed(realSeed());

        assertThat(second).isEqualTo(new SeedSummary(0, 0, 0, 0));
        assertThat(users.count()).isEqualTo(3);
        assertThat(playlists.count()).isEqualTo(10);
        assertThat(entries.count()).isEqualTo(50);
        assertThat(ratings.count()).isEqualTo(20);
    }

    @Test
    void deletedPlaylistsAreImportedAndMarkedAsDeleted() {
        service.importSeed(realSeed());

        assertThat(playlists.findByExternalId("pl-03").get().isDeleted()).isTrue();
        assertThat(playlists.findByExternalId("pl-07").get().isDeleted()).isTrue();
        assertThat(playlists.findAll().stream().filter(Playlist::isDeleted).count()).isEqualTo(2);
    }

    @Test
    void filmsThatOnlyExistInDeletedPlaylistsAreImportedToo() {
        service.importSeed(realSeed());

        List<PlaylistMovie> filmsOfDeletedPlaylists = entries.findAll().stream()
                .filter(entry -> entry.getPlaylist().isDeleted())
                .toList();

        assertThat(filmsOfDeletedPlaylists).extracting(PlaylistMovie::getTmdbId)
                .contains(289L, 348L, 550L, 807L);
    }

    @Test
    void aFilmListedTwiceInOnePlaylistIsStoredOnce() {
        service.importSeed(realSeed());

        long timesIn27205 = entries.findAll().stream()
                .filter(entry -> entry.getPlaylist().getExternalId().equals("pl-01"))
                .filter(entry -> entry.getTmdbId().equals(27205L))
                .count();

        assertThat(timesIn27205).isEqualTo(1);
    }

    @Test
    void whenAFilmIsListedTwiceTheLowestOrderWins() {
        SeedData seed = smallSeed(List.of(
                new SeedData.SeedFilm(27205L, 5),
                new SeedData.SeedFilm(603L, 2),
                new SeedData.SeedFilm(27205L, 1)));

        service.importSeed(seed);

        assertThat(entries.count()).isEqualTo(2);
        PlaylistMovie kept = entries.findAll().stream()
                .filter(entry -> entry.getTmdbId().equals(27205L))
                .findFirst()
                .get();
        assertThat(kept.getSortOrder()).isEqualTo(1);
    }

    @Test
    void reimportingDoesNotOverwriteChangesMadeInTheApp() {
        SeedData seed = smallSeed(List.of(new SeedData.SeedFilm(27205L, 1)));
        service.importSeed(seed);

        Playlist playlist = playlists.findByExternalId("pl-01").get();
        playlist.setName("Renamed by the user");
        playlist.setDeleted(true);
        playlists.save(playlist);
        UserRating rating = ratings.findByUserAndTmdbId(users.findByName("ana").get(), 27205L).get();
        rating.setStars(3);
        ratings.save(rating);

        service.importSeed(seed);

        Playlist afterwards = playlists.findByExternalId("pl-01").get();
        assertThat(afterwards.getName()).isEqualTo("Renamed by the user");
        assertThat(afterwards.isDeleted()).isTrue();
        UserRating ratingAfterwards = ratings.findByUserAndTmdbId(users.findByName("ana").get(), 27205L).get();
        assertThat(ratingAfterwards.getStars()).isEqualTo(3);
    }

    @Test
    void aPlaylistOwnedByAnUnknownUserIsRejected() {
        SeedData seed = new SeedData(
                List.of(new SeedData.SeedUser("ana")),
                List.of(new SeedData.SeedPlaylist("pl-01", "Sci-fi", "zoe", false, List.of())),
                List.of());

        assertThatThrownBy(() -> service.importSeed(seed))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("zoe");
    }
}