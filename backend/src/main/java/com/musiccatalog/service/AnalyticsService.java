package com.musiccatalog.service;

import com.musiccatalog.dto.AnalyticsResponse;
import com.musiccatalog.dto.AnalyticsResponse.*;
import com.musiccatalog.repository.AlbumRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    private final AlbumRepository albumRepository;

    public AnalyticsService(AlbumRepository albumRepository) {
        this.albumRepository = albumRepository;
    }

    public AnalyticsResponse getAnalytics(Long userId) {
        AnalyticsResponse response = new AnalyticsResponse();

        // Summary stats
        response.setTotalAlbums((int) albumRepository.countByUserId(userId));
        response.setTotalArtists((int) albumRepository.countDistinctArtistsByUserId(userId));
        response.setTotalGenres((int) albumRepository.countDistinctGenresByUserId(userId));

        Double avgRating = albumRepository.averageRatingByUserId(userId);
        response.setAverageRating(avgRating != null ? Math.round(avgRating * 10.0) / 10.0 : 0.0);

        // Genre distribution (Pie chart)
        List<Object[]> genreData = albumRepository.countByGenreGrouped(userId);
        response.setGenreDistribution(genreData.stream()
                .map(row -> new GenreCount((String) row[0], (Long) row[1]))
                .collect(Collectors.toList()));

        // Top artists (Bar chart)
        List<Object[]> artistData = albumRepository.countByArtistGrouped(userId);
        response.setTopArtists(artistData.stream()
                .limit(10)
                .map(row -> new ArtistCount((String) row[0], (Long) row[1]))
                .collect(Collectors.toList()));

        // Release year distribution (Histogram)
        List<Object[]> yearData = albumRepository.countByReleaseYearGrouped(userId);
        response.setReleasesByYear(yearData.stream()
                .map(row -> new YearCount(((Number) row[0]).intValue(), (Long) row[1]))
                .collect(Collectors.toList()));

        // Rating distribution (Horizontal bar)
        List<Object[]> ratingData = albumRepository.countByRatingGrouped(userId);
        response.setRatingDistribution(ratingData.stream()
                .map(row -> new RatingCount(((Number) row[0]).intValue(), (Long) row[1]))
                .collect(Collectors.toList()));

        // Library growth (Line chart)
        try {
            List<Object[]> growthData = albumRepository.countByCreatedMonthGrouped(userId);
            response.setLibraryGrowth(growthData.stream()
                    .map(row -> new MonthCount((String) row[0], (Long) row[1]))
                    .collect(Collectors.toList()));
        } catch (Exception e) {
            response.setLibraryGrowth(new ArrayList<>());
        }

        return response;
    }
}
