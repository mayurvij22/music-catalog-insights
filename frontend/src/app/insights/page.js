'use client';

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { useAuth } from '@/lib/auth';
import { useToast } from '@/lib/toast';
import { aiApi } from '@/lib/api';
import styles from './insights.module.css';

export default function InsightsPage() {
  const [insights, setInsights] = useState(null);
  const [loading, setLoading] = useState(false);
  const [fetched, setFetched] = useState(false);
  const { isAuthenticated, loading: authLoading } = useAuth();
  const toast = useToast();
  const router = useRouter();

  useEffect(() => {
    if (!authLoading && !isAuthenticated()) {
      router.push('/auth');
    }
  }, [authLoading, isAuthenticated, router]);

  const fetchInsights = async () => {
    setLoading(true);
    try {
      const response = await aiApi.getRecommendations();
      setInsights(response.data);
      setFetched(true);
    } catch (err) {
      toast.error('Failed to get AI recommendations');
    } finally {
      setLoading(false);
    }
  };

  if (authLoading) {
    return <div className="loading-container"><div className="spinner"></div></div>;
  }

  return (
    <div className="page-container">
      <div className="page-header">
        <h1 className="page-title">✨ AI Insights</h1>
        <p className="page-subtitle">Get personalized album recommendations based on your library</p>
      </div>

      {!fetched && !loading && (
        <div className={styles.heroSection}>
          <div className={styles.heroCard}>
            <div className={styles.heroIcon}>🤖</div>
            <h2 className={styles.heroTitle}>Smart Recommendations</h2>
            <p className={styles.heroText}>
              Our AI analyzes your library's genres, artists, and ratings to suggest albums
              you'll love. Click below to get started!
            </p>
            <button className="btn btn-primary" onClick={fetchInsights} style={{ marginTop: '1.5rem', fontSize: '1rem', padding: '0.875rem 2rem' }}>
              ✨ Generate Recommendations
            </button>

            <div className={styles.features}>
              <div className={styles.feature}>
                <span className={styles.featureIcon}>🎯</span>
                <span>Genre Analysis</span>
              </div>
              <div className={styles.feature}>
                <span className={styles.featureIcon}>🎤</span>
                <span>Artist Patterns</span>
              </div>
              <div className={styles.feature}>
                <span className={styles.featureIcon}>📅</span>
                <span>Era Preferences</span>
              </div>
              <div className={styles.feature}>
                <span className={styles.featureIcon}>⭐</span>
                <span>Rating Trends</span>
              </div>
            </div>
          </div>
        </div>
      )}

      {loading && (
        <div className={styles.loadingState}>
          <div className={styles.aiLoader}>
            <div className={styles.aiLoaderDot}></div>
            <div className={styles.aiLoaderDot}></div>
            <div className={styles.aiLoaderDot}></div>
          </div>
          <h3 className={styles.loadingTitle}>Analyzing your library...</h3>
          <p className={styles.loadingText}>Our AI is studying your music taste patterns</p>
        </div>
      )}

      {fetched && insights && !loading && (
        <div className={styles.resultsSection}>
          {/* Taste Analysis */}
          {insights.tasteAnalysis && (
            <div className={styles.analysisCard}>
              <div className="ai-badge">🧠 AI Analysis</div>
              <h3 className={styles.analysisTitle}>Your Music Taste Profile</h3>
              <p className={styles.analysisText}>{insights.tasteAnalysis}</p>
            </div>
          )}

          {insights.message && !insights.recommendations?.length && (
            <div className="empty-state">
              <div className="empty-state-icon">📚</div>
              <h2 className="empty-state-title">{insights.message}</h2>
              <button className="btn btn-primary" onClick={() => router.push('/search')} style={{ marginTop: '1rem' }}>
                🔍 Search Albums
              </button>
            </div>
          )}

          {/* Recommendations */}
          {insights.recommendations?.length > 0 && (
            <>
              <h2 className={styles.sectionTitle}>🎵 Recommended Albums</h2>
              <div className={styles.recommendationsGrid}>
                {insights.recommendations.map((rec, index) => (
                  <div key={index} className="ai-card">
                    <div className="ai-badge">✨ #{index + 1} Pick</div>
                    <h3 className={styles.recAlbum}>{rec.album}</h3>
                    <p className={styles.recArtist}>{rec.artist}</p>
                    <div className={styles.recMeta}>
                      {rec.genre && <span className="badge badge-genre">{rec.genre}</span>}
                      {rec.year && <span className={styles.recYear}>{rec.year}</span>}
                    </div>
                    <p className={styles.recReason}>{rec.reason}</p>
                  </div>
                ))}
              </div>
            </>
          )}

          {/* Source Badge */}
          <div className={styles.sourceInfo}>
            <span className={styles.sourceLabel}>
              Powered by {insights.source === 'gemini' ? '🧠 Google Gemini AI' : '🔄 Smart Algorithm'}
            </span>
            <button className="btn btn-secondary btn-sm" onClick={fetchInsights}>
              🔄 Refresh
            </button>
          </div>
        </div>
      )}
    </div>
  );
}
