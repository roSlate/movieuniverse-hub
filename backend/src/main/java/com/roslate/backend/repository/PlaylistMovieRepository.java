package com.roslate.backend.repository;
import com.roslate.backend.model.Playlist;
import com.roslate.backend.model.PlaylistMovie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlaylistMovieRepository extends JpaRepository<PlaylistMovie, Long> {
    boolean existsByPlaylistAndTmdbId(Playlist playlist, Long tmdbId);

    List<PlaylistMovie> findByPlaylistOrderBySortOrder(Playlist playlist);

    Optional<PlaylistMovie> findByPlaylistAndTmdbId(Playlist playlist, Long tmdbId);
}