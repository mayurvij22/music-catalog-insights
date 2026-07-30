package com.musiccatalog.service;

import com.musiccatalog.model.Album;
import com.musiccatalog.repository.AlbumRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AiService {

    private final AlbumRepository albumRepository;
    private final WebClient webClient;

    @Value("${gemini.api.key:}")
    private String geminiApiKey;

    public AiService(AlbumRepository albumRepository, WebClient.Builder webClientBuilder) {
        this.albumRepository = albumRepository;
        this.webClient = webClientBuilder
                .baseUrl("https://generativelanguage.googleapis.com")
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(10 * 1024 * 1024))
                .build();
    }

    public Map<String, Object> getRecommendations(Long userId) {
        List<Album> library = albumRepository.findByUserId(userId);

        if (library.isEmpty()) {
            Map<String, Object> result = new HashMap<>();
            result.put("message", "Add some albums to your library first to get AI recommendations!");
            result.put("recommendations", new ArrayList<>());
            return result;
        }

        String libraryProfile = buildLibraryProfile(library);

        if (geminiApiKey == null || geminiApiKey.isBlank()) {
            return generateFallbackRecommendations(library);
        }

        try {
            return callGeminiApi(libraryProfile);
        } catch (Exception e) {
            return generateFallbackRecommendations(library);
        }
    }

    private String buildLibraryProfile(List<Album> library) {
        Map<String, Long> genreCounts = library.stream()
                .filter(a -> a.getGenre() != null)
                .collect(Collectors.groupingBy(Album::getGenre, Collectors.counting()));

        Map<String, Long> artistCounts = library.stream()
                .collect(Collectors.groupingBy(Album::getArtistName, Collectors.counting()));

        Set<String> decades = library.stream()
                .filter(a -> a.getReleaseDate() != null)
                .map(a -> (a.getReleaseDate().getYear() / 10 * 10) + "s")
                .collect(Collectors.toSet());

        double avgRating = library.stream()
                .filter(a -> a.getUserRating() != null)
                .mapToInt(Album::getUserRating)
                .average()
                .orElse(0);

        StringBuilder profile = new StringBuilder();
        profile.append("User's music library has ").append(library.size()).append(" albums.\n");
        profile.append("Genres: ").append(genreCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .map(e -> e.getKey() + " (" + e.getValue() + ")")
                .collect(Collectors.joining(", "))).append("\n");
        profile.append("Artists: ").append(artistCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(10)
                .map(e -> e.getKey() + " (" + e.getValue() + ")")
                .collect(Collectors.joining(", "))).append("\n");
        profile.append("Decades: ").append(String.join(", ", decades)).append("\n");
        profile.append("Average rating: ").append(String.format("%.1f", avgRating)).append("/5\n");
        profile.append("Sample albums: ").append(library.stream()
                .limit(10)
                .map(a -> a.getTitle() + " by " + a.getArtistName())
                .collect(Collectors.joining(", ")));

        return profile.toString();
    }

    private Map<String, Object> callGeminiApi(String libraryProfile) {
        String prompt = "Based on this user's music library profile, suggest exactly 5 albums they might enjoy. " +
                "For each recommendation, provide: album name, artist name, genre, year, and a brief reason why they'd like it " +
                "(based on their existing library patterns). " +
                "Also provide a brief overall analysis of their music taste.\n\n" +
                "Library Profile:\n" + libraryProfile + "\n\n" +
                "Respond in this exact JSON format:\n" +
                "{\n" +
                "  \"tasteAnalysis\": \"brief analysis of music taste\",\n" +
                "  \"recommendations\": [\n" +
                "    {\"album\": \"name\", \"artist\": \"name\", \"genre\": \"genre\", \"year\": \"year\", \"reason\": \"why\"}\n" +
                "  ]\n" +
                "}";

        Map<String, Object> requestBody = new HashMap<>();
        List<Map<String, Object>> contents = new ArrayList<>();
        Map<String, Object> content = new HashMap<>();
        List<Map<String, String>> parts = new ArrayList<>();
        Map<String, String> part = new HashMap<>();
        part.put("text", prompt);
        parts.add(part);
        content.put("parts", parts);
        contents.add(content);
        requestBody.put("contents", contents);

        // Add generation config for JSON output
        Map<String, Object> generationConfig = new HashMap<>();
        generationConfig.put("temperature", 0.7);
        generationConfig.put("maxOutputTokens", 2048);
        requestBody.put("generationConfig", generationConfig);

        String response = webClient.post()
                .uri("/v1beta/models/gemini-2.0-flash:generateContent?key=" + geminiApiKey)
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return parseGeminiResponse(response);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> parseGeminiResponse(String response) {
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            Map<String, Object> geminiResponse = mapper.readValue(response, Map.class);

            List<Map<String, Object>> candidates = (List<Map<String, Object>>) geminiResponse.get("candidates");
            if (candidates != null && !candidates.isEmpty()) {
                Map<String, Object> candidate = candidates.get(0);
                Map<String, Object> content = (Map<String, Object>) candidate.get("content");
                List<Map<String, String>> parts = (List<Map<String, String>>) content.get("parts");
                String text = parts.get(0).get("text");

                // Extract JSON from the response text (it might be wrapped in markdown code blocks)
                String jsonStr = text;
                if (text.contains("```json")) {
                    jsonStr = text.substring(text.indexOf("```json") + 7);
                    jsonStr = jsonStr.substring(0, jsonStr.indexOf("```"));
                } else if (text.contains("```")) {
                    jsonStr = text.substring(text.indexOf("```") + 3);
                    jsonStr = jsonStr.substring(0, jsonStr.indexOf("```"));
                }

                Map<String, Object> result = mapper.readValue(jsonStr.trim(), Map.class);
                result.put("source", "gemini");
                return result;
            }
        } catch (Exception e) {
            // Fall through to error response
        }

        Map<String, Object> errorResult = new HashMap<>();
        errorResult.put("message", "Failed to parse AI response");
        errorResult.put("recommendations", new ArrayList<>());
        return errorResult;
    }

    private Map<String, Object> generateFallbackRecommendations(List<Album> library) {
        Map<String, Object> result = new HashMap<>();
        result.put("source", "algorithm");
        result.put("tasteAnalysis", buildTasteAnalysis(library));

        // Generate recommendations based on patterns
        List<Map<String, String>> recommendations = new ArrayList<>();

        Map<String, Long> genreCounts = library.stream()
                .filter(a -> a.getGenre() != null)
                .collect(Collectors.groupingBy(Album::getGenre, Collectors.counting()));

        String topGenre = genreCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Pop");

        // Curated recommendations by genre
        Map<String, List<Map<String, String>>> genreRecommendations = new HashMap<>();

        genreRecommendations.put("Alternative", List.of(
                Map.of("album", "OK Computer", "artist", "Radiohead", "genre", "Alternative", "year", "1997", "reason", "A landmark alternative rock album that matches your taste for the genre"),
                Map.of("album", "Is This It", "artist", "The Strokes", "genre", "Alternative", "year", "2001", "reason", "Essential indie rock that complements your alternative collection"),
                Map.of("album", "The Bends", "artist", "Radiohead", "genre", "Alternative", "year", "1995", "reason", "Another Radiohead masterpiece with guitar-driven alternative sound")
        ));

        genreRecommendations.put("Pop", List.of(
                Map.of("album", "Future Nostalgia", "artist", "Dua Lipa", "genre", "Pop", "year", "2020", "reason", "A critically acclaimed pop album with retro-futuristic vibes"),
                Map.of("album", "After Hours", "artist", "The Weeknd", "genre", "Pop", "year", "2020", "reason", "Blends pop with dark R&B atmospherics"),
                Map.of("album", "folklore", "artist", "Taylor Swift", "genre", "Pop", "year", "2020", "reason", "An intimate indie-folk departure that showcases songwriting mastery")
        ));

        genreRecommendations.put("Rock", List.of(
                Map.of("album", "Abbey Road", "artist", "The Beatles", "genre", "Rock", "year", "1969", "reason", "The quintessential rock album — a must for any rock enthusiast"),
                Map.of("album", "Rumours", "artist", "Fleetwood Mac", "genre", "Rock", "year", "1977", "reason", "Classic rock perfection with timeless songwriting"),
                Map.of("album", "AM", "artist", "Arctic Monkeys", "genre", "Rock", "year", "2013", "reason", "Modern rock with swagger and hooks")
        ));

        // Default recommendations
        List<Map<String, String>> defaults = List.of(
                Map.of("album", "Random Access Memories", "artist", "Daft Punk", "genre", "Electronic", "year", "2013", "reason", "A genre-defining electronic album with universal appeal"),
                Map.of("album", "Channel Orange", "artist", "Frank Ocean", "genre", "R&B/Soul", "year", "2012", "reason", "Critically acclaimed R&B that transcends genre boundaries"),
                Map.of("album", "To Pimp a Butterfly", "artist", "Kendrick Lamar", "genre", "Hip-Hop", "year", "2015", "reason", "A masterpiece blending jazz, funk, and hip-hop"),
                Map.of("album", "In Rainbows", "artist", "Radiohead", "genre", "Alternative", "year", "2007", "reason", "Beautifully crafted alternative rock with electronic elements"),
                Map.of("album", "Blonde", "artist", "Frank Ocean", "genre", "R&B/Soul", "year", "2016", "reason", "An avant-garde R&B album with wide appeal")
        );

        List<Map<String, String>> genreSpecific = genreRecommendations.getOrDefault(topGenre, new ArrayList<>());
        recommendations.addAll(genreSpecific);

        // Fill remaining slots from defaults
        for (Map<String, String> rec : defaults) {
            if (recommendations.size() >= 5) break;
            boolean alreadyAdded = recommendations.stream()
                    .anyMatch(r -> r.get("album").equals(rec.get("album")));
            if (!alreadyAdded) {
                recommendations.add(rec);
            }
        }

        result.put("recommendations", recommendations.subList(0, Math.min(5, recommendations.size())));
        return result;
    }

    private String buildTasteAnalysis(List<Album> library) {
        Map<String, Long> genreCounts = library.stream()
                .filter(a -> a.getGenre() != null)
                .collect(Collectors.groupingBy(Album::getGenre, Collectors.counting()));

        String topGenre = genreCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("diverse");

        long uniqueArtists = library.stream()
                .map(Album::getArtistName)
                .distinct()
                .count();

        return String.format(
                "Your library contains %d albums across %d genres from %d different artists. " +
                "You show a strong preference for %s music. " +
                "Your collection suggests a well-curated taste with an appreciation for quality over quantity.",
                library.size(), genreCounts.size(), uniqueArtists, topGenre
        );
    }
}
