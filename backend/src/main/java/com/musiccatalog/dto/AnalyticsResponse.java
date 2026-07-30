package com.musiccatalog.dto;

import java.util.List;
import java.util.Map;

public class AnalyticsResponse {

    private int totalAlbums;
    private int totalArtists;
    private int totalGenres;
    private double averageRating;
    private List<GenreCount> genreDistribution;
    private List<ArtistCount> topArtists;
    private List<YearCount> releasesByYear;
    private List<RatingCount> ratingDistribution;
    private List<MonthCount> libraryGrowth;

    // Inner DTOs
    public static class GenreCount {
        private String genre;
        private long count;
        public GenreCount(String genre, long count) { this.genre = genre; this.count = count; }
        public String getGenre() { return genre; }
        public long getCount() { return count; }
    }

    public static class ArtistCount {
        private String artist;
        private long count;
        public ArtistCount(String artist, long count) { this.artist = artist; this.count = count; }
        public String getArtist() { return artist; }
        public long getCount() { return count; }
    }

    public static class YearCount {
        private int year;
        private long count;
        public YearCount(int year, long count) { this.year = year; this.count = count; }
        public int getYear() { return year; }
        public long getCount() { return count; }
    }

    public static class RatingCount {
        private int rating;
        private long count;
        public RatingCount(int rating, long count) { this.rating = rating; this.count = count; }
        public int getRating() { return rating; }
        public long getCount() { return count; }
    }

    public static class MonthCount {
        private String month;
        private long count;
        public MonthCount(String month, long count) { this.month = month; this.count = count; }
        public String getMonth() { return month; }
        public long getCount() { return count; }
    }

    // Getters and Setters
    public int getTotalAlbums() { return totalAlbums; }
    public void setTotalAlbums(int totalAlbums) { this.totalAlbums = totalAlbums; }

    public int getTotalArtists() { return totalArtists; }
    public void setTotalArtists(int totalArtists) { this.totalArtists = totalArtists; }

    public int getTotalGenres() { return totalGenres; }
    public void setTotalGenres(int totalGenres) { this.totalGenres = totalGenres; }

    public double getAverageRating() { return averageRating; }
    public void setAverageRating(double averageRating) { this.averageRating = averageRating; }

    public List<GenreCount> getGenreDistribution() { return genreDistribution; }
    public void setGenreDistribution(List<GenreCount> genreDistribution) { this.genreDistribution = genreDistribution; }

    public List<ArtistCount> getTopArtists() { return topArtists; }
    public void setTopArtists(List<ArtistCount> topArtists) { this.topArtists = topArtists; }

    public List<YearCount> getReleasesByYear() { return releasesByYear; }
    public void setReleasesByYear(List<YearCount> releasesByYear) { this.releasesByYear = releasesByYear; }

    public List<RatingCount> getRatingDistribution() { return ratingDistribution; }
    public void setRatingDistribution(List<RatingCount> ratingDistribution) { this.ratingDistribution = ratingDistribution; }

    public List<MonthCount> getLibraryGrowth() { return libraryGrowth; }
    public void setLibraryGrowth(List<MonthCount> libraryGrowth) { this.libraryGrowth = libraryGrowth; }
}
