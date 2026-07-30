'use client';

import { useState, useEffect, useCallback, useRef } from 'react';
import { useRouter } from 'next/navigation';
import { useAuth } from '@/lib/auth';
import { useToast } from '@/lib/toast';
import { searchApi, libraryApi } from '@/lib/api';
import AlbumCard from '@/components/AlbumCard';
import styles from './search.module.css';

export default function SearchPage() {
  const [query, setQuery] = useState('');
  const [results, setResults] = useState([]);
  const [loading, setLoading] = useState(false);
  const [searched, setSearched] = useState(false);
  const [addingIds, setAddingIds] = useState(new Set());
  const { isAuthenticated, loading: authLoading } = useAuth();
  const toast = useToast();
  const router = useRouter();
  const debounceRef = useRef(null);

  useEffect(() => {
    if (!authLoading && !isAuthenticated()) {
      router.push('/auth');
    }
  }, [authLoading, isAuthenticated, router]);

  // Debounced search
  const debouncedSearch = useCallback((searchQuery) => {
    if (debounceRef.current) {
      clearTimeout(debounceRef.current);
    }

    if (!searchQuery.trim()) {
      setResults([]);
      setSearched(false);
      return;
    }

    debounceRef.current = setTimeout(async () => {
      setLoading(true);
      setSearched(true);
      try {
        const response = await searchApi.search(searchQuery, 30);
        const albums = response.data.results || [];
        setResults(albums);
      } catch (err) {
        toast.error('Search failed. Please try again.');
        setResults([]);
      } finally {
        setLoading(false);
      }
    }, 300);
  }, [toast]);

  const handleQueryChange = (e) => {
    const value = e.target.value;
    setQuery(value);
    debouncedSearch(value);
  };

  const handleAddToLibrary = async (album) => {
    const catalogId = album.collectionId;
    if (addingIds.has(catalogId)) return;

    setAddingIds((prev) => new Set(prev).add(catalogId));

    try {
      await libraryApi.add({
        appleCatalogId: catalogId,
        title: album.collectionName || 'Unknown Album',
        artistName: album.artistName || 'Unknown Artist',
        genre: album.primaryGenreName || null,
        releaseDate: album.releaseDate || null,
        trackCount: album.trackCount || 0,
        artworkUrl: album.artworkUrl100 || '',
        collectionPrice: album.collectionPrice || null,
      });
      toast.success(`"${album.collectionName}" added to library!`);
    } catch (err) {
      const message = err.response?.data?.message || 'Failed to add album';
      toast.error(message);
    } finally {
      setAddingIds((prev) => {
        const next = new Set(prev);
        next.delete(catalogId);
        return next;
      });
    }
  };

  if (authLoading) {
    return (
      <div className="loading-container">
        <div className="spinner"></div>
      </div>
    );
  }

  return (
    <div className="page-container">
      <div className="page-header">
        <h1 className="page-title">🔍 Search Albums</h1>
        <p className="page-subtitle">Discover albums from the iTunes catalog and add them to your library</p>
      </div>

      <div className={styles.searchSection}>
        <div className={styles.searchBar}>
          <span className={styles.searchIcon}>🔍</span>
          <input
            type="text"
            className={styles.searchInput}
            placeholder="Search for albums, artists..."
            value={query}
            onChange={handleQueryChange}
            autoFocus
          />
          {loading && <div className={styles.searchSpinner}></div>}
          {query && (
            <button
              className={styles.clearBtn}
              onClick={() => { setQuery(''); setResults([]); setSearched(false); }}
            >
              ✕
            </button>
          )}
        </div>

        {query && (
          <p className={styles.resultCount}>
            {loading ? 'Searching...' : `${results.length} album${results.length !== 1 ? 's' : ''} found`}
          </p>
        )}
      </div>

      {!searched && !loading && (
        <div className="empty-state">
          <div className="empty-state-icon">🎶</div>
          <h2 className="empty-state-title">Start Your Music Journey</h2>
          <p className="empty-state-text">
            Search for your favorite albums by name or artist and build your personal music library
          </p>
        </div>
      )}

      {searched && !loading && results.length === 0 && (
        <div className="empty-state">
          <div className="empty-state-icon">😢</div>
          <h2 className="empty-state-title">No Albums Found</h2>
          <p className="empty-state-text">
            Try a different search term or check your spelling
          </p>
        </div>
      )}

      <div className="grid-auto">
        {results.map((album, index) => (
          <AlbumCard
            key={album.collectionId || index}
            album={album}
            mode="search"
            onAdd={handleAddToLibrary}
          />
        ))}
      </div>
    </div>
  );
}
