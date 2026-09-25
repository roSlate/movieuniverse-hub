package com.roslate.backend.seed;

import com.roslate.backend.model.AppUser;
import com.roslate.backend.model.Playlist;
import com.roslate.backend.model.PlaylistMovie;
import com.roslate.backend.model.UserRating;
import com.roslate.backend.repository.AppUserRepository;
import com.roslate.backend.repository.PlaylistMovieRepository;
import com.roslate.backend.repository.PlaylistRepository;
import com.roslate.backend.repository.UserRatingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

/**
 * Imports the seed data (users, playlists, films and ratings) into the database.
 * Safe to run more than once: anything that already exists is skipped.
 */
@Service
public class SeedImportService {

    private final AppUserRepository users;
    private final PlaylistRepository playlists;
    private final PlaylistMovieRepository entries;
    private final UserRatingRepository ratings;

    public SeedImportService(AppUserRepository users, PlaylistRepository playlists,
                             PlaylistMovieRepository entries, UserRatingRepository ratings) {
        this.users = users;
        this.playlists = playlists;
        this.entries = entries;
        this.ratings = ratings;
    }

    /**
     * Saves the contents of the seed, creating only what does not exist yet.
     *
     * @param data the parsed seed file
     * @return how many users, playlists, films and ratings were created
     */
    @Transactional
    public SeedSummary importSeed(SeedData data) {
        int usersAdded = 0;
        int playlistsAdded = 0;
        int filmsAdded = 0;
        int ratingsAdded = 0;

        //User portion
        for (SeedData.SeedUser seedUser : data.utilizadores()) {
            if (users.findByName(seedUser.nome()).isEmpty()) {
                users.save(new AppUser(seedUser.nome()));
                usersAdded++;
            }
        }

        //Playlist portion
        for (SeedData.SeedPlaylist seedPlaylist : data.playlists()) {
            AppUser owner = findUser(seedPlaylist.utilizador());
            Playlist playlist = playlists.findByExternalId(seedPlaylist.id()).orElse(null);
            if (playlist == null) {
                playlist = playlists.save(
                        new Playlist(seedPlaylist.id(), seedPlaylist.nome(), owner, seedPlaylist.apagada()));
                playlistsAdded++;
            }

            //Film portion
            List<SeedData.SeedFilm> films = seedPlaylist.filmes().stream()
                    .sorted(Comparator.comparingInt(SeedData.SeedFilm::ordem))
                    .toList();
            for (SeedData.SeedFilm film : films) {
                if (!entries.existsByPlaylistAndTmdbId(playlist, film.tmdbId())) {
                    entries.save(new PlaylistMovie(playlist, film.tmdbId(), film.ordem()));
                    filmsAdded++;
                }
            }
        }

        //Rating portion
        for (SeedData.SeedRating seedRating : data.notas()) {
            AppUser user = findUser(seedRating.utilizador());
            if (ratings.findByUserAndTmdbId(user, seedRating.tmdbId()).isEmpty()) {
                ratings.save(new UserRating(user, seedRating.tmdbId(), seedRating.estrelas(), seedRating.data()));
                ratingsAdded++;
            }
        }

        return new SeedSummary(usersAdded, playlistsAdded, filmsAdded, ratingsAdded);
    }


    private AppUser findUser(String name) {
        return users.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("Seed refers to an unknown user: " + name));
    }
}