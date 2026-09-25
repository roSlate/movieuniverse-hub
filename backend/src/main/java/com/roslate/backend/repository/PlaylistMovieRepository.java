package com.roslate.backend.repository;
import com.roslate.backend.model.Playlist;
import com.roslate.backend.model.PlaylistMovie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaylistMovieRepository extends JpaRepository<PlaylistMovie, Long> {
    boolean existsByPlaylistAndTmdbId(Playlist playlist, Long tmdbId);
}