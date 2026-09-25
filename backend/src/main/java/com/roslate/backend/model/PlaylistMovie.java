package com.roslate.backend.model;

import jakarta.persistence.*;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"playlist_id", "tmdb_id"}))
public class PlaylistMovie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Playlist playlist;

    @Column(name = "tmdb_id", nullable = false)
    private Long tmdbId;

    @Column(nullable = false)
    private int sortOrder;

    protected PlaylistMovie() {
    }

    public PlaylistMovie(Playlist playlist, Long tmdbId, int sortOrder) {
        this.playlist = playlist;
        this.tmdbId = tmdbId;
        this.sortOrder = sortOrder;
    }

    public Long getId() {
        return id;
    }

    public Playlist getPlaylist() {
        return playlist;
    }

    public Long getTmdbId() {
        return tmdbId;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }
}