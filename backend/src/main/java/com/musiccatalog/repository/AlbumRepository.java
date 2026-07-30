package com.musiccatalog.repository;

import com.musiccatalog.model.Album;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {

    Page<Album> findByUserId(Long userId, Pageable pageable);

    List<Album> findByUserId(Long userId);

    Optional<Album> findByIdAndUserId(Long id, Long userId);

    Optional<Album> findByUserIdAndAppleCatalogId(Long userId, Long appleCatalogId);

    boolean existsByUserIdAndAppleCatalogId(Long userId, Long appleCatalogId);

    long countByUserId(Long userId);

    // Analytics queries
    @Query("SELECT a.genre, COUNT(a) FROM Album a WHERE a.userId = :userId AND a.genre IS NOT NULL GROUP BY a.genre ORDER BY COUNT(a) DESC")
    List<Object[]> countByGenreGrouped(@Param("userId") Long userId);

    @Query("SELECT a.artistName, COUNT(a) FROM Album a WHERE a.userId = :userId GROUP BY a.artistName ORDER BY COUNT(a) DESC")
    List<Object[]> countByArtistGrouped(@Param("userId") Long userId);

    @Query("SELECT YEAR(a.releaseDate), COUNT(a) FROM Album a WHERE a.userId = :userId AND a.releaseDate IS NOT NULL GROUP BY YEAR(a.releaseDate) ORDER BY YEAR(a.releaseDate)")
    List<Object[]> countByReleaseYearGrouped(@Param("userId") Long userId);

    @Query("SELECT a.userRating, COUNT(a) FROM Album a WHERE a.userId = :userId AND a.userRating IS NOT NULL GROUP BY a.userRating ORDER BY a.userRating")
    List<Object[]> countByRatingGrouped(@Param("userId") Long userId);

    @Query("SELECT FUNCTION('FORMATDATETIME', a.createdAt, 'yyyy-MM'), COUNT(a) FROM Album a WHERE a.userId = :userId GROUP BY FUNCTION('FORMATDATETIME', a.createdAt, 'yyyy-MM') ORDER BY FUNCTION('FORMATDATETIME', a.createdAt, 'yyyy-MM')")
    List<Object[]> countByCreatedMonthGrouped(@Param("userId") Long userId);

    @Query("SELECT COUNT(DISTINCT a.artistName) FROM Album a WHERE a.userId = :userId")
    long countDistinctArtistsByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(DISTINCT a.genre) FROM Album a WHERE a.userId = :userId AND a.genre IS NOT NULL")
    long countDistinctGenresByUserId(@Param("userId") Long userId);

    @Query("SELECT AVG(a.userRating) FROM Album a WHERE a.userId = :userId AND a.userRating IS NOT NULL")
    Double averageRatingByUserId(@Param("userId") Long userId);
}
