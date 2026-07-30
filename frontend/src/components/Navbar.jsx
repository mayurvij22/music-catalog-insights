'use client';

import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { useAuth } from '@/lib/auth';
import styles from './Navbar.module.css';

export default function Navbar() {
  const pathname = usePathname();
  const { user, logout, isAuthenticated } = useAuth();

  if (!isAuthenticated() || pathname === '/auth') return null;

  const navLinks = [
    { href: '/search', label: 'Search', icon: '🔍' },
    { href: '/library', label: 'Library', icon: '📚' },
    { href: '/analytics', label: 'Analytics', icon: '📊' },
    { href: '/insights', label: 'AI Insights', icon: '✨' },
  ];

  return (
    <nav className={styles.navbar}>
      <div className={styles.navContent}>
        <Link href="/search" className={styles.logo}>
          <span className={styles.logoIcon}>🎵</span>
          <span className={styles.logoText}>MusicCatalog</span>
        </Link>

        <div className={styles.navLinks}>
          {navLinks.map((link) => (
            <Link
              key={link.href}
              href={link.href}
              className={`${styles.navLink} ${pathname === link.href ? styles.active : ''}`}
            >
              <span className={styles.navIcon}>{link.icon}</span>
              <span className={styles.navLabel}>{link.label}</span>
            </Link>
          ))}
        </div>

        <div className={styles.userMenu}>
          <span className={styles.username}>👤 {user?.username}</span>
          <button onClick={logout} className={styles.logoutBtn}>
            Logout
          </button>
        </div>
      </div>
    </nav>
  );
}
