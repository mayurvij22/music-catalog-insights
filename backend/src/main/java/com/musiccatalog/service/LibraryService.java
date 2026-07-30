package com.musiccatalog.service;

import com.musiccatalog.dto.AlbumRequest;
import com.musiccatalog.dto.AlbumResponse;
import com.musiccatalog.dto.AlbumUpdateRequest;
import com.musiccatalog.exception.GlobalExceptionHandler.DuplicateResourceException;
import com.musiccatalog.exception.GlobalExceptionHandler.ResourceNotFoundException;
import com.musiccatalog.model.Album;
import com.musiccatalog.model.User;
import com.musiccatalog.repository.AlbumRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Service
public class LibraryService {

    private final AlbumRepository albumRepository;

    public LibraryService(AlbumRepository albumRepository) {
        this.albumRepository = albumRepository;
    }

    public Page<AlbumResponse> getUserLibrary(Long userId, int page, int size, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return albumRepository.findByUserId(userId, pageable).map(AlbumResponse::fromEntity);
    }

    public AlbumResponse addToLibrary(Long userId, AlbumRequest request) {
        if (albumRepository.existsByUserIdAndAppleCatalogId(userId, request.getAppleCatalogId())) {
            throw new DuplicateResourceException("Album already exists in your library");
        }

        Album album = new Album();
        album.setUserId(userId);
        album.setAppleCatalogId(request.getAppleCatalogId());
        album.setTitle(request.getTitle());
        album.setArtistName(request.getArtistName());
        album.setGenre(request.getGenre());
        album.setTrackCount(request.getTrackCount());
        album.setArtworkUrl(request.getArtworkUrl());
        album.setCollectionPrice(request.getCollectionPrice());
        album.setUserRating(request.getUserRating());
        album.setUserNotes(request.getUserNotes());

        if (request.getReleaseDate() != null) {
            album.setReleaseDate(parseDate(request.getReleaseDate()));
        }

        Album saved = albumRepository.save(album);
        return AlbumResponse.fromEntity(saved);
    }

    public AlbumResponse updateAlbum(Long userId, Long albumId, AlbumUpdateRequest request) {
        Album album = albumRepository.findByIdAndUserId(albumId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Album not found in your library"));

        if (request.getUserRating() != null) {
            album.setUserRating(request.getUserRating());
        }
        if (request.getUserNotes() != null) {
            album.setUserNotes(request.getUserNotes());
        }

        Album updated = albumRepository.save(album);
        return AlbumResponse.fromEntity(updated);
    }

    public void deleteAlbum(Long userId, Long albumId) {
        Album album = albumRepository.findByIdAndUserId(albumId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Album not found in your library"));
        albumRepository.delete(album);
    }

    public AlbumResponse getAlbum(Long userId, Long albumId) {
        Album album = albumRepository.findByIdAndUserId(albumId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Album not found in your library"));
        return AlbumResponse.fromEntity(album);
    }

    private LocalDateTime parseDate(String dateStr) {
        try {
            // iTunes format: "2000-07-10T12:00:00Z"
            return LocalDateTime.parse(dateStr.replace("Z", ""),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        } catch (DateTimeParseException e) {
            try {
                return LocalDateTime.parse(dateStr + "T00:00:00",
                        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
            } catch (DateTimeParseException e2) {
                return null;
            }
        }
    }
}
