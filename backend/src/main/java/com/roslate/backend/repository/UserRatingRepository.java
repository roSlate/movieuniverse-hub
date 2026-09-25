package com.roslate.backend.repository;

import com.roslate.backend.model.AppUser;
import com.roslate.backend.model.UserRating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRatingRepository extends JpaRepository<UserRating, Long> {
    Optional<UserRating> findByUserAndTmdbId(AppUser user, Long tmdbId);

    List<UserRating> findByTmdbId(Long tmdbId);
}