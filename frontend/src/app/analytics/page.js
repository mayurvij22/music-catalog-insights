'use client';

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { useAuth } from '@/lib/auth';
import { useToast } from '@/lib/toast';
import { analyticsApi } from '@/lib/api';
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarElement,
  LineElement,
  PointElement,
  ArcElement,
  Title,
  Tooltip,
  Legend,
  Filler,
} from 'chart.js';
import { Bar, Pie, Line, Doughnut } from 'react-chartjs-2';
import styles from './analytics.module.css';

ChartJS.register(
  CategoryScale, LinearScale, BarElement, LineElement,
  PointElement, ArcElement, Title, Tooltip, Legend, Filler
);

// Chart colors
const COLORS = [
  '#8b5cf6', '#6366f1', '#3b82f6', '#06b6d4', '#10b981',
  '#f59e0b', '#ec4899', '#ef4444', '#a78bfa', '#818cf8',
  '#38bdf8', '#34d399', '#fbbf24', '#f472b6', '#fb7185',
];

const CHART_FONT = { family: "'Inter', sans-serif", color: '#a0a0cc' };

const commonOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: { labels: { color: '#a0a0cc', font: { family: "'Inter', sans-serif", size: 11 } } },
    tooltip: {
      backgroundColor: 'rgba(17, 17, 40, 0.95)',
      titleColor: '#f0f0ff',
      bodyColor: '#a0a0cc',
      borderColor: 'rgba(139, 92, 246, 0.3)',
      borderWidth: 1,
      cornerRadius: 8,
      padding: 12,
      titleFont: { family: "'Inter', sans-serif" },
      bodyFont: { family: "'Inter', sans-serif" },
    },
  },
  scales: {
    x: { ticks: { color: '#6b6b99', font: { family: "'Inter', sans-serif", size: 11 } }, grid: { color: 'rgba(139, 92, 246, 0.08)' } },
    y: { ticks: { color: '#6b6b99', font: { family: "'Inter', sans-serif", size: 11 } }, grid: { color: 'rgba(139, 92, 246, 0.08)' } },
  },
};

const pieOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: {
      position: 'right',
      labels: { color: '#a0a0cc', font: { family: "'Inter', sans-serif", size: 11 }, padding: 15, usePointStyle: true },
    },
    tooltip: commonOptions.plugins.tooltip,
  },
};

export default function AnalyticsPage() {
  const [analytics, setAnalytics] = useState(null);
  const [loading, setLoading] = useState(true);
  const { isAuthenticated, loading: authLoading } = useAuth();
  const toast = useToast();
  const router = useRouter();

  useEffect(() => {
    if (!authLoading && !isAuthenticated()) {
      router.push('/auth');
    }
  }, [authLoading, isAuthenticated, router]);

  useEffect(() => {
    if (!authLoading && isAuthenticated()) {
      fetchAnalytics();
    }
  }, [authLoading, isAuthenticated]);

  const fetchAnalytics = async () => {
    try {
      const response = await analyticsApi.get();
      setAnalytics(response.data);
    } catch (err) {
      toast.error('Failed to load analytics');
    } finally {
      setLoading(false);
    }
  };

  if (authLoading || loading) {
    return (
      <div className="loading-container" style={{ minHeight: '100vh' }}>
        <div className="spinner"></div>
        <p>Loading analytics...</p>
      </div>
    );
  }

  if (!analytics || analytics.totalAlbums === 0) {
    return (
      <div className="page-container">
        <div className="page-header">
          <h1 className="page-title">📊 Analytics</h1>
        </div>
        <div className="empty-state">
          <div className="empty-state-icon">📊</div>
          <h2 className="empty-state-title">No Data Yet</h2>
          <p className="empty-state-text">
            Add albums to your library to see analytics and insights
          </p>
          <button className="btn btn-primary" onClick={() => router.push('/search')} style={{ marginTop: '1rem' }}>
            🔍 Search Albums
          </button>
        </div>
      </div>
    );
  }

  // Chart Data
  const genreData = {
    labels: analytics.genreDistribution?.map((g) => g.genre) || [],
    datasets: [{
      data: analytics.genreDistribution?.map((g) => g.count) || [],
      backgroundColor: COLORS.slice(0, analytics.genreDistribution?.length || 0),
      borderColor: 'transparent',
      borderWidth: 0,
    }],
  };

  const artistData = {
    labels: analytics.topArtists?.map((a) => a.artist) || [],
    datasets: [{
      label: 'Albums',
      data: analytics.topArtists?.map((a) => a.count) || [],
      backgroundColor: 'rgba(139, 92, 246, 0.6)',
      borderColor: '#8b5cf6',
      borderWidth: 1,
      borderRadius: 6,
    }],
  };

  const yearData = {
    labels: analytics.releasesByYear?.map((y) => y.year) || [],
    datasets: [{
      label: 'Albums Released',
      data: analytics.releasesByYear?.map((y) => y.count) || [],
      backgroundColor: 'rgba(99, 102, 241, 0.5)',
      borderColor: '#6366f1',
      borderWidth: 1,
      borderRadius: 4,
    }],
  };

  const ratingData = {
    labels: analytics.ratingDistribution?.map((r) => `${r.rating} Star${r.rating > 1 ? 's' : ''}`) || [],
    datasets: [{
      label: 'Albums',
      data: analytics.ratingDistribution?.map((r) => r.count) || [],
      backgroundColor: [
        'rgba(239, 68, 68, 0.6)', 'rgba(245, 158, 11, 0.6)',
        'rgba(234, 179, 8, 0.6)', 'rgba(16, 185, 129, 0.6)', 'rgba(139, 92, 246, 0.6)',
      ],
      borderColor: ['#ef4444', '#f59e0b', '#eab308', '#10b981', '#8b5cf6'],
      borderWidth: 1,
      borderRadius: 6,
    }],
  };

  const growthData = {
    labels: analytics.libraryGrowth?.map((m) => m.month) || [],
    datasets: [{
      label: 'Albums Added',
      data: analytics.libraryGrowth?.map((m) => m.count) || [],
      borderColor: '#8b5cf6',
      backgroundColor: 'rgba(139, 92, 246, 0.1)',
      fill: true,
      tension: 0.4,
      pointBackgroundColor: '#8b5cf6',
      pointBorderColor: '#fff',
      pointBorderWidth: 2,
      pointRadius: 5,
    }],
  };

  return (
    <div className="page-container">
      <div className="page-header">
        <h1 className="page-title">📊 Analytics Dashboard</h1>
        <p className="page-subtitle">Insights into your music collection</p>
      </div>

      {/* Stats Row */}
      <div className="grid-4" style={{ marginBottom: '2rem' }}>
        <div className="stat-card">
          <div className="stat-value">{analytics.totalAlbums}</div>
          <div className="stat-label">Total Albums</div>
        </div>
        <div className="stat-card">
          <div className="stat-value">{analytics.totalArtists}</div>
          <div className="stat-label">Artists</div>
        </div>
        <div className="stat-card">
          <div className="stat-value">{analytics.totalGenres}</div>
          <div className="stat-label">Genres</div>
        </div>
        <div className="stat-card">
          <div className="stat-value">{analytics.averageRating > 0 ? `${analytics.averageRating}★` : '—'}</div>
          <div className="stat-label">Avg Rating</div>
        </div>
      </div>

      {/* Charts */}
      <div className="grid-2">
        {/* Genre Distribution - Donut */}
        {analytics.genreDistribution?.length > 0 && (
          <div className="chart-card">
            <div className="chart-title">🍩 Genre Distribution</div>
            <div className="chart-container">
              <Doughnut data={genreData} options={{
                ...pieOptions,
                cutout: '60%',
              }} />
            </div>
          </div>
        )}

        {/* Top Artists - Bar */}
        {analytics.topArtists?.length > 0 && (
          <div className="chart-card">
            <div className="chart-title">🎤 Top Artists</div>
            <div className="chart-container">
              <Bar data={artistData} options={{
                ...commonOptions,
                indexAxis: 'y',
                plugins: { ...commonOptions.plugins, legend: { display: false } },
              }} />
            </div>
          </div>
        )}

        {/* Release Year - Histogram */}
        {analytics.releasesByYear?.length > 0 && (
          <div className="chart-card">
            <div className="chart-title">📅 Releases by Year</div>
            <div className="chart-container">
              <Bar data={yearData} options={{
                ...commonOptions,
                plugins: { ...commonOptions.plugins, legend: { display: false } },
              }} />
            </div>
          </div>
        )}

        {/* Rating Distribution - Bar */}
        {analytics.ratingDistribution?.length > 0 && (
          <div className="chart-card">
            <div className="chart-title">⭐ Rating Distribution</div>
            <div className="chart-container">
              <Bar data={ratingData} options={{
                ...commonOptions,
                plugins: { ...commonOptions.plugins, legend: { display: false } },
              }} />
            </div>
          </div>
        )}
      </div>

      {/* Library Growth - Full Width Line */}
      {analytics.libraryGrowth?.length > 0 && (
        <div className="chart-card" style={{ marginTop: '1.5rem' }}>
          <div className="chart-title">📈 Library Growth</div>
          <div className="chart-container" style={{ minHeight: '300px' }}>
            <Line data={growthData} options={{
              ...commonOptions,
              plugins: { ...commonOptions.plugins, legend: { display: false } },
            }} />
          </div>
        </div>
      )}
    </div>
  );
}
