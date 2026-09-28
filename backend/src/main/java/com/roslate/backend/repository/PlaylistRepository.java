package com.roslate.backend.repository;

import com.roslate.backend.model.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

import com.roslate.backend.model.AppUser;

public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
    Optional<Playlist> findByExternalId(String externalId);

    List<Playlist> findByOwnerAndDeletedFalse(AppUser owner);
}