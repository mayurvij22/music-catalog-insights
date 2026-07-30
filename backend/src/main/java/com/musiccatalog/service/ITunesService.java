package com.musiccatalog.service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ITunesService {

    private final WebClient webClient;

    public ITunesService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("https://itunes.apple.com")
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(10 * 1024 * 1024))
                .build();
    }

    @Cacheable(value = "itunesSearch", key = "#query + '_' + #limit")
    public Map<String, Object> searchAlbums(String query, int limit) {
        try {
            String response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/search")
                            .queryParam("term", query)
                            .queryParam("entity", "album")
                            .queryParam("media", "music")
                            .queryParam("limit", Math.min(limit, 200))
                            .build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return parseSearchResponse(response);
        } catch (Exception e) {
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("resultCount", 0);
            errorResult.put("results", new ArrayList<>());
            errorResult.put("error", "Failed to search iTunes: " + e.getMessage());
            return errorResult;
        }
    }

    @Cacheable(value = "itunesLookup", key = "#id")
    public Map<String, Object> lookupById(Long id) {
        try {
            String response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/lookup")
                            .queryParam("id", id)
                            .build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return parseSearchResponse(response);
        } catch (Exception e) {
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("resultCount", 0);
            errorResult.put("results", new ArrayList<>());
            return errorResult;
        }
    }

    private Map<String, Object> parseSearchResponse(String json) {
        // Manual JSON parsing to avoid complex deserialization issues
        // Spring's Jackson will handle the final serialization
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            return mapper.readValue(json, Map.class);
        } catch (Exception e) {
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("resultCount", 0);
            errorResult.put("results", new ArrayList<>());
            return errorResult;
        }
    }
}
