'use client';

import { useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { useAuth } from '@/lib/auth';

export default function Home() {
  const router = useRouter();
  const { isAuthenticated, loading } = useAuth();

  useEffect(() => {
    if (!loading) {
      if (isAuthenticated()) {
        router.push('/search');
      } else {
        router.push('/auth');
      }
    }
  }, [loading, isAuthenticated, router]);

  return (
    <div className="loading-container">
      <div className="spinner"></div>
      <p>Loading MusicCatalog...</p>
    </div>
  );
}
