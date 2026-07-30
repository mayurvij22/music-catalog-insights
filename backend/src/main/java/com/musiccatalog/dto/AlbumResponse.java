package com.musiccatalog.dto;

import com.musiccatalog.model.Album;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AlbumResponse {

    private Long id;
    private Long appleCatalogId;
    private String title;
    private String artistName;
    private String genre;
    private LocalDateTime releaseDate;
    private Integer trackCount;
    private String artworkUrl;
    private BigDecimal collectionPrice;
    private Integer userRating;
    private String userNotes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static AlbumResponse fromEntity(Album album) {
        AlbumResponse response = new AlbumResponse();
        response.setId(album.getId());
        response.setAppleCatalogId(album.getAppleCatalogId());
        response.setTitle(album.getTitle());
        response.setArtistName(album.getArtistName());
        response.setGenre(album.getGenre());
        response.setReleaseDate(album.getReleaseDate());
        response.setTrackCount(album.getTrackCount());
        response.setArtworkUrl(album.getArtworkUrl());
        response.setCollectionPrice(album.getCollectionPrice());
        response.setUserRating(album.getUserRating());
        response.setUserNotes(album.getUserNotes());
        response.setCreatedAt(album.getCreatedAt());
        response.setUpdatedAt(album.getUpdatedAt());
        return response;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getAppleCatalogId() { return appleCatalogId; }
    public void setAppleCatalogId(Long appleCatalogId) { this.appleCatalogId = appleCatalogId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getArtistName() { return artistName; }
    public void setArtistName(String artistName) { this.artistName = artistName; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public LocalDateTime getReleaseDate() { return releaseDate; }
    public void setReleaseDate(LocalDateTime releaseDate) { this.releaseDate = releaseDate; }

    public Integer getTrackCount() { return trackCount; }
    public void setTrackCount(Integer trackCount) { this.trackCount = trackCount; }

    public String getArtworkUrl() { return artworkUrl; }
    public void setArtworkUrl(String artworkUrl) { this.artworkUrl = artworkUrl; }

    public BigDecimal getCollectionPrice() { return collectionPrice; }
    public void setCollectionPrice(BigDecimal collectionPrice) { this.collectionPrice = collectionPrice; }

    public Integer getUserRating() { return userRating; }
    public void setUserRating(Integer userRating) { this.userRating = userRating; }

    public String getUserNotes() { return userNotes; }
    public void setUserNotes(String userNotes) { this.userNotes = userNotes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
