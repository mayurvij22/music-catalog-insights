'use client';

import styles from './AlbumCard.module.css';

export default function AlbumCard({ album, mode = 'search', onAdd, onEdit, onDelete }) {
  const artworkUrl = album.artworkUrl100 || album.artworkUrl || album.artwork_url || '';
  const highResArtwork = artworkUrl.replace('100x100', '300x300');
  const title = album.collectionName || album.title || 'Unknown Album';
  const artist = album.artistName || album.artist_name || 'Unknown Artist';
  const genre = album.primaryGenreName || album.genre || '';
  const trackCount = album.trackCount || album.track_count || 0;
  const price = album.collectionPrice || album.collection_price;
  const releaseDate = album.releaseDate || album.release_date;
  const year = releaseDate ? new Date(releaseDate).getFullYear() : '';
  const userRating = album.userRating || album.user_rating;

  const renderStars = (rating) => {
    return '★'.repeat(rating) + '☆'.repeat(5 - rating);
  };

  return (
    <div className={styles.card}>
      <div className={styles.artworkContainer}>
        {highResArtwork ? (
          <img
            src={highResArtwork}
            alt={title}
            className={styles.artwork}
            loading="lazy"
          />
        ) : (
          <div className={styles.artworkPlaceholder}>🎵</div>
        )}
        <div className={styles.artworkOverlay}>
          {mode === 'search' && onAdd && (
            <button className={`btn btn-primary btn-sm ${styles.addBtn}`} onClick={() => onAdd(album)}>
              + Add to Library
            </button>
          )}
        </div>
      </div>

      <div className={styles.info}>
        <h3 className={styles.title} title={title}>{title}</h3>
        <p className={styles.artist}>{artist}</p>

        <div className={styles.meta}>
          {genre && <span className="badge badge-genre">{genre}</span>}
          {year && <span className={styles.year}>{year}</span>}
        </div>

        <div className={styles.details}>
          {trackCount > 0 && <span>{trackCount} tracks</span>}
          {price && price > 0 && <span>${Number(price).toFixed(2)}</span>}
        </div>

        {userRating && (
          <div className={styles.rating}>
            <span className={styles.stars}>{renderStars(userRating)}</span>
          </div>
        )}

        {album.userNotes && (
          <p className={styles.notes} title={album.userNotes}>
            💬 {album.userNotes.substring(0, 50)}{album.userNotes.length > 50 ? '...' : ''}
          </p>
        )}

        {mode === 'library' && (
          <div className={styles.actions}>
            <button className="btn btn-secondary btn-sm" onClick={() => onEdit && onEdit(album)}>
              ✏️ Edit
            </button>
            <button className="btn btn-danger btn-sm" onClick={() => onDelete && onDelete(album)}>
              🗑️ Remove
            </button>
          </div>
        )}
      </div>
    </div>
  );
}
