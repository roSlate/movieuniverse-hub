package com.roslate.backend.playlist;

import com.roslate.backend.model.AppUser;
import com.roslate.backend.model.Playlist;
import com.roslate.backend.model.PlaylistMovie;
import com.roslate.backend.repository.AppUserRepository;
import com.roslate.backend.repository.PlaylistMovieRepository;
import com.roslate.backend.repository.PlaylistRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;

/**
 * Manages playlists and the films inside them. Users are identified only by name: the first time a name is
 * used, a user is created for it.
 */
@Service
public class PlaylistService {

    private final AppUserRepository users;
    private final PlaylistRepository playlists;
    private final PlaylistMovieRepository entries;

    public PlaylistService(AppUserRepository users, PlaylistRepository playlists, PlaylistMovieRepository entries) {
        this.users = users;
        this.playlists = playlists;
        this.entries = entries;
    }

    public List<PlaylistSummary> listPlaylists(String userName) {
        AppUser user = getOrCreateUser(userName);
        return playlists.findByOwnerAndDeletedFalse(user).stream()
                .map(p -> new PlaylistSummary(p.getId(), p.getName()))
                .toList();
    }

    public PlaylistSummary createPlaylist(String userName, String name) {
        AppUser user = getOrCreateUser(userName);
        Playlist playlist = playlists.save(new Playlist(null, name, user, false));
        return new PlaylistSummary(playlist.getId(), playlist.getName());
    }

    public PlaylistDetail getPlaylist(long playlistId) {
        Playlist playlist = findPlaylist(playlistId);
        List<Long> tmdbIds = entries.findByPlaylistOrderBySortOrder(playlist).stream()
                .map(PlaylistMovie::getTmdbId)
                .toList();
        return new PlaylistDetail(playlist.getId(), playlist.getName(), tmdbIds);
    }

    public void addFilm(long playlistId, long tmdbId) {
        Playlist playlist = findPlaylist(playlistId);
        if (!entries.existsByPlaylistAndTmdbId(playlist, tmdbId)) {
            int nextOrder = entries.findByPlaylistOrderBySortOrder(playlist).size() + 1;
            entries.save(new PlaylistMovie(playlist, tmdbId, nextOrder));
        }
    }

    public void removeFilm(long playlistId, long tmdbId) {
        Playlist playlist = findPlaylist(playlistId);
        entries.findByPlaylistAndTmdbId(playlist, tmdbId).ifPresent(entries::delete);
    }

    private AppUser getOrCreateUser(String name) {
        return users.findByName(name).orElseGet(() -> users.save(new AppUser(name)));
    }

    private Playlist findPlaylist(long playlistId) {
        return playlists.findById(playlistId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Playlist not found"));
    }
}