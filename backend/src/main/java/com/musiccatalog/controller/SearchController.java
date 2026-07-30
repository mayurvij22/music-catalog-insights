package com.musiccatalog.controller;

import com.musiccatalog.service.ITunesService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final ITunesService iTunesService;

    public SearchController(ITunesService iTunesService) {
        this.iTunesService = iTunesService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> search(
            @RequestParam String query,
            @RequestParam(defaultValue = "album") String type,
            @RequestParam(defaultValue = "25") int limit) {

        if (query == null || query.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Query parameter is required",
                    "resultCount", 0
            ));
        }

        Map<String, Object> results = iTunesService.searchAlbums(query.trim(), limit);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/lookup/{id}")
    public ResponseEntity<Map<String, Object>> lookup(@PathVariable Long id) {
        Map<String, Object> result = iTunesService.lookupById(id);
        return ResponseEntity.ok(result);
    }
}
