import './globals.css';
import { AuthProvider } from '@/lib/auth';
import { ToastProvider } from '@/lib/toast';
import Navbar from '@/components/Navbar';

export const metadata = {
  title: 'MusicCatalog — Your Personal Album Insights Platform',
  description: 'Search, save, and analyze your personal music album library with AI-driven insights. Powered by the iTunes catalog.',
  keywords: 'music, albums, catalog, analytics, AI, recommendations',
};

export default function RootLayout({ children }) {
  return (
    <html lang="en">
      <body>
        <AuthProvider>
          <ToastProvider>
            <Navbar />
            <main>{children}</main>
          </ToastProvider>
        </AuthProvider>
      </body>
    </html>
  );
}
