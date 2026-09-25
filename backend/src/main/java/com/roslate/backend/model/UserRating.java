package com.roslate.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.time.LocalDate;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "tmdb_id"}))
public class UserRating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private AppUser user;

    @Column(name = "tmdb_id", nullable = false)
    private Long tmdbId;

    @Min(1)
    @Max(10)
    private int stars;

    @Column(nullable = false)
    private LocalDate ratedAt;

    protected UserRating() {
    }

    public UserRating(AppUser user, Long tmdbId, int stars, LocalDate ratedAt) {
        this.user = user;
        this.tmdbId = tmdbId;
        this.stars = stars;
        this.ratedAt = ratedAt;
    }

    public Long getId() {
        return id;
    }

    public AppUser getUser() {
        return user;
    }

    public Long getTmdbId() {
        return tmdbId;
    }

    public int getStars() {
        return stars;
    }

    public LocalDate getRatedAt() {
        return ratedAt;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public void setRatedAt(LocalDate ratedAt) {
        this.ratedAt = ratedAt;
    }
}