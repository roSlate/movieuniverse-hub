package com.roslate.backend.playlist;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PlaylistController {

    private final PlaylistService playlistService;

    public PlaylistController(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }

    @GetMapping("/api/users/{name}/playlists")
    public List<PlaylistSummary> listPlaylists(@PathVariable String name) {
        return playlistService.listPlaylists(name);
    }

    @PostMapping("/api/users/{name}/playlists")
    public PlaylistSummary createPlaylist(@PathVariable String name, @RequestBody CreatePlaylistRequest request) {
        return playlistService.createPlaylist(name, request.name());
    }

    @GetMapping("/api/playlists/{id}")
    public PlaylistDetail getPlaylist(@PathVariable long id) {
        return playlistService.getPlaylist(id);
    }

    @PutMapping("/api/playlists/{id}/movies/{tmdbId}")
    public void addFilm(@PathVariable long id, @PathVariable long tmdbId) {
        playlistService.addFilm(id, tmdbId);
    }

    @DeleteMapping("/api/playlists/{id}/movies/{tmdbId}")
    public void removeFilm(@PathVariable long id, @PathVariable long tmdbId) {
        playlistService.removeFilm(id, tmdbId);
    }
}