'use client';

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { useAuth } from '@/lib/auth';
import { useToast } from '@/lib/toast';
import { libraryApi } from '@/lib/api';
import AlbumCard from '@/components/AlbumCard';
import styles from './library.module.css';

export default function LibraryPage() {
  const [albums, setAlbums] = useState([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [editAlbum, setEditAlbum] = useState(null);
  const [deleteAlbum, setDeleteAlbum] = useState(null);
  const [editForm, setEditForm] = useState({ userRating: null, userNotes: '' });
  const { isAuthenticated, loading: authLoading } = useAuth();
  const toast = useToast();
  const router = useRouter();

  useEffect(() => {
    if (!authLoading && !isAuthenticated()) {
      router.push('/auth');
    }
  }, [authLoading, isAuthenticated, router]);

  const fetchLibrary = async (pageNum = 0) => {
    setLoading(true);
    try {
      const response = await libraryApi.getAll(pageNum, 20);
      setAlbums(response.data.content || []);
      setTotalPages(response.data.totalPages || 0);
      setTotalElements(response.data.totalElements || 0);
      setPage(pageNum);
    } catch (err) {
      toast.error('Failed to load library');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (!authLoading && isAuthenticated()) {
      fetchLibrary();
    }
  }, [authLoading, isAuthenticated]);

  const handleEdit = (album) => {
    setEditAlbum(album);
    setEditForm({
      userRating: album.userRating || null,
      userNotes: album.userNotes || '',
    });
  };

  const handleSaveEdit = async () => {
    try {
      await libraryApi.update(editAlbum.id, editForm);
      toast.success('Album updated!');
      setEditAlbum(null);
      fetchLibrary(page);
    } catch (err) {
      toast.error('Failed to update album');
    }
  };

  const handleDelete = async () => {
    try {
      await libraryApi.delete(deleteAlbum.id);
      toast.success(`"${deleteAlbum.title}" removed from library`);
      setDeleteAlbum(null);
      fetchLibrary(page);
    } catch (err) {
      toast.error('Failed to remove album');
    }
  };

  if (authLoading) {
    return <div className="loading-container"><div className="spinner"></div></div>;
  }

  return (
    <div className="page-container">
      <div className="page-header">
        <h1 className="page-title">📚 My Library</h1>
        <p className="page-subtitle">
          {totalElements > 0
            ? `${totalElements} album${totalElements !== 1 ? 's' : ''} in your collection`
            : 'Your personal album collection'}
        </p>
      </div>

      {loading ? (
        <div className="loading-container">
          <div className="spinner"></div>
          <p>Loading your library...</p>
        </div>
      ) : albums.length === 0 ? (
        <div className="empty-state">
          <div className="empty-state-icon">📚</div>
          <h2 className="empty-state-title">Your Library is Empty</h2>
          <p className="empty-state-text">
            Start by searching for albums and adding them to your library
          </p>
          <button className="btn btn-primary" onClick={() => router.push('/search')} style={{ marginTop: '1rem' }}>
            🔍 Search Albums
          </button>
        </div>
      ) : (
        <>
          <div className="grid-auto">
            {albums.map((album) => (
              <AlbumCard
                key={album.id}
                album={album}
                mode="library"
                onEdit={handleEdit}
                onDelete={(a) => setDeleteAlbum(a)}
              />
            ))}
          </div>

          {totalPages > 1 && (
            <div className="pagination">
              <button
                className="pagination-btn"
                onClick={() => fetchLibrary(page - 1)}
                disabled={page === 0}
              >
                ← Previous
              </button>
              <span className="pagination-info">
                Page {page + 1} of {totalPages}
              </span>
              <button
                className="pagination-btn"
                onClick={() => fetchLibrary(page + 1)}
                disabled={page >= totalPages - 1}
              >
                Next →
              </button>
            </div>
          )}
        </>
      )}

      {/* Edit Modal */}
      {editAlbum && (
        <div className="modal-overlay" onClick={() => setEditAlbum(null)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <h2 className="modal-title">✏️ Edit Album</h2>

            <div className={styles.editAlbumInfo}>
              <img
                src={editAlbum.artworkUrl?.replace('100x100', '200x200') || ''}
                alt={editAlbum.title}
                className={styles.editArtwork}
              />
              <div>
                <h3 className={styles.editTitle}>{editAlbum.title}</h3>
                <p className={styles.editArtist}>{editAlbum.artistName}</p>
              </div>
            </div>

            <div className="input-group">
              <label className="input-label">Rating</label>
              <div className={styles.ratingInput}>
                {[1, 2, 3, 4, 5].map((star) => (
                  <button
                    key={star}
                    className={`${styles.starBtn} ${editForm.userRating >= star ? styles.starActive : ''}`}
                    onClick={() => setEditForm({ ...editForm, userRating: star })}
                    type="button"
                  >
                    {editForm.userRating >= star ? '★' : '☆'}
                  </button>
                ))}
                {editForm.userRating && (
                  <button
                    className={styles.clearRating}
                    onClick={() => setEditForm({ ...editForm, userRating: null })}
                    type="button"
                  >
                    Clear
                  </button>
                )}
              </div>
            </div>

            <div className="input-group">
              <label className="input-label">Notes</label>
              <textarea
                className="input-field"
                placeholder="Add your thoughts about this album..."
                value={editForm.userNotes}
                onChange={(e) => setEditForm({ ...editForm, userNotes: e.target.value })}
                rows={3}
              />
            </div>

            <div className="modal-actions">
              <button className="btn btn-secondary" onClick={() => setEditAlbum(null)}>
                Cancel
              </button>
              <button className="btn btn-primary" onClick={handleSaveEdit}>
                Save Changes
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Delete Confirmation Modal */}
      {deleteAlbum && (
        <div className="modal-overlay" onClick={() => setDeleteAlbum(null)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <h2 className="modal-title">🗑️ Remove Album</h2>
            <p style={{ color: 'var(--text-secondary)', marginBottom: '1rem' }}>
              Are you sure you want to remove <strong>"{deleteAlbum.title}"</strong> by {deleteAlbum.artistName} from your library?
            </p>
            <div className="modal-actions">
              <button className="btn btn-secondary" onClick={() => setDeleteAlbum(null)}>
                Cancel
              </button>
              <button className="btn btn-danger" onClick={handleDelete}>
                Remove
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
