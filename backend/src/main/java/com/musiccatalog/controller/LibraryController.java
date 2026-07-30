package com.musiccatalog.controller;

import com.musiccatalog.dto.AlbumRequest;
import com.musiccatalog.dto.AlbumResponse;
import com.musiccatalog.dto.AlbumUpdateRequest;
import com.musiccatalog.model.User;
import com.musiccatalog.service.LibraryService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/library")
public class LibraryController {

    private final LibraryService libraryService;

    public LibraryController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    @GetMapping
    public ResponseEntity<Page<AlbumResponse>> getLibrary(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        Page<AlbumResponse> library = libraryService.getUserLibrary(
                user.getId(), page, size, sortBy, direction);
        return ResponseEntity.ok(library);
    }

    @PostMapping
    public ResponseEntity<AlbumResponse> addToLibrary(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody AlbumRequest request) {

        AlbumResponse response = libraryService.addToLibrary(user.getId(), request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AlbumResponse> updateAlbum(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @Valid @RequestBody AlbumUpdateRequest request) {

        AlbumResponse response = libraryService.updateAlbum(user.getId(), id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlbum(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {

        libraryService.deleteAlbum(user.getId(), id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlbumResponse> getAlbum(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {

        AlbumResponse response = libraryService.getAlbum(user.getId(), id);
        return ResponseEntity.ok(response);
    }
}
