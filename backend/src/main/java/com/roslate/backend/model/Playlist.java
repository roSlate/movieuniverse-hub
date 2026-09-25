package com.roslate.backend.model;

import jakarta.persistence.*;

@Entity
public class Playlist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // seed id such as "pl-01", and null for playlists created in the app
    @Column(unique = true)
    private String externalId;

    @Column(nullable = false)
    private String name;

    @ManyToOne(optional = false)
    private AppUser owner;

    private boolean deleted;

    protected Playlist() {
    }

    public Playlist(String externalId, String name, AppUser owner, boolean deleted) {
        this.externalId = externalId;
        this.name = name;
        this.owner = owner;
        this.deleted = deleted;
    }

    public Long getId() {
        return id;
    }

    public String getExternalId() {
        return externalId;
    }

    public String getName() {
        return name;
    }

    public AppUser getOwner() {
        return owner;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}