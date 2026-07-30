# Music Catalog Insights Platform 🎵

A full-stack web application that lets users search a public music catalog (iTunes), save albums into a personal library, view rich analytics dashboards, and receive AI-driven music recommendations.

**Live Demo**: _[To be added after deployment]_

---

## 📌 Entity Choice: Albums

**Why Albums?** Albums provide the richest dataset for analytics and AI features:
- **Multiple data dimensions**: artist, genre, release date, track count, price, artwork
- **Rich analytics potential**: genre distribution, timeline analysis, price ranges, artist frequency
- **AI-friendly**: patterns across genres, eras, and artists enable meaningful recommendations
- **Natural cataloging unit**: users naturally collect and rate albums

---

## 🏗️ Architecture

```
┌──────────────────────┐     ┌────────────────────────┐     ┌──────────────────┐
│   Next.js Frontend   │────▶│  Spring Boot Backend   │────▶│  iTunes Search   │
│   (React + Chart.js) │     │  (Java 17 + JPA)       │     │  API (Apple)     │
└──────────────────────┘     └────────────┬───────────┘     └──────────────────┘
                                          │
                              ┌───────────┴───────────┐
                              │   H2 / PostgreSQL     │
                              │   Database            │
                              └───────────────────────┘
```

---

## 🗄️ Database Schema

**Choice: SQL (H2 / PostgreSQL)** — Relational data with structured fields benefits from SQL's strong typing, JOIN capabilities, and aggregate functions needed for analytics queries.

```sql
CREATE TABLE users (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(50)  UNIQUE NOT NULL,
    email       VARCHAR(100) UNIQUE NOT NULL,
    password    VARCHAR(255) NOT NULL,  -- bcrypt hashed
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE library_albums (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id          BIGINT NOT NULL REFERENCES users(id),
    apple_catalog_id BIGINT NOT NULL,
    title            VARCHAR(500) NOT NULL,
    artist_name      VARCHAR(500) NOT NULL,
    genre            VARCHAR(100),
    release_date     TIMESTAMP,
    track_count      INTEGER,
    artwork_url      VARCHAR(1000),
    collection_price DECIMAL(10,2),
    user_rating      INTEGER CHECK (user_rating BETWEEN 1 AND 5),
    user_notes       TEXT,
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (user_id, apple_catalog_id)
);
```

---

## 🔌 REST API

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/api/auth/register` | ❌ | Register new user |
| POST | `/api/auth/login` | ❌ | Login, returns JWT |
| GET | `/api/search?query=...&limit=25` | ❌ | Search iTunes catalog |
| GET | `/api/library?page=0&size=20` | ✅ | List user's albums (paginated) |
| POST | `/api/library` | ✅ | Add album to library |
| PUT | `/api/library/{id}` | ✅ | Update rating/notes |
| DELETE | `/api/library/{id}` | ✅ | Remove album |
| GET | `/api/analytics` | ✅ | Library analytics data |
| GET | `/api/ai/recommendations` | ✅ | AI recommendations |

---

## 🤖 AI Feature: Smart Recommendations

**How it works:**
1. Analyzes the user's library: genre distribution, artist patterns, era preferences, ratings
2. Builds a structured "music taste profile" 
3. Sends the profile to **Google Gemini API** for personalized album recommendations
4. Returns 5 recommended albums with reasoning for each

**Fallback:** When no Gemini API key is configured, uses a smart algorithmic fallback that recommends based on the user's top genres with curated suggestions.

---

## 📊 Analytics Charts

| Chart | Type | Data Visualized |
|-------|------|----------------|
| Genre Distribution | Donut | Percentage breakdown by genre |
| Top Artists | Horizontal Bar | Top 10 artists by album count |
| Releases by Year | Bar (Histogram) | Album release year distribution |
| Rating Distribution | Bar | Count of albums per star rating |
| Library Growth | Line (Area) | Albums added over time |

---

## 🛠️ Tech Stack

| Layer | Technology | Justification |
|-------|-----------|---------------|
| Backend | Java 17 + Spring Boot 3.2 | Required by spec; mature ecosystem |
| Database | H2 (dev) / PostgreSQL (prod) | SQL for structured queries + analytics aggregations |
| Frontend | Next.js 14 + React | Required by spec; SSR-ready, Vercel-native |
| Charts | Chart.js + react-chartjs-2 | Lightweight, supports all 5 chart types |
| Auth | JWT (jjwt library) | Stateless authentication as required |
| AI | Google Gemini API | Free tier, generates natural language recommendations |
| HTTP Client | WebClient (Spring WebFlux) | Non-blocking HTTP for iTunes API calls |
| Caching | Caffeine | High-performance in-memory cache for search results |

---

## 🚀 Setup Instructions

### Prerequisites
- Java 17+
- Node.js 18+
- Maven (or use the included wrapper)

### Backend
```bash
cd backend
# If Maven is installed:
mvn spring-boot:run

# Or use wrapper (Unix):
./mvnw spring-boot:run

# Or use wrapper (Windows):
mvnw.cmd spring-boot:run
```
Backend runs on `http://localhost:8080`

### Frontend
```bash
cd frontend
npm install
npm run dev
```
Frontend runs on `http://localhost:3000`

### Environment Variables

**Backend** (`application.properties`):
```properties
gemini.api.key=YOUR_GEMINI_API_KEY  # Optional - get from https://aistudio.google.com/
```

**Frontend** (`.env.local`):
```
NEXT_PUBLIC_API_URL=http://localhost:8080
```

---

## ⚖️ Trade-offs & Decisions

| Decision | Trade-off |
|----------|-----------|
| **H2 over PostgreSQL for dev** | Zero-setup local development vs. in-memory data loss on restart |
| **Albums over Songs** | Richer analytics data vs. larger catalog with songs |
| **Caffeine cache** | Fast search results vs. potential stale data (10min TTL) |
| **Fallback AI** | Works without API key vs. less personalized than Gemini |
| **JWT in localStorage** | Simple implementation vs. XSS vulnerability (httpOnly cookie is more secure) |
| **Client-side charts** | Interactive rendering vs. server-side generation |

---

## ✅ Features Checklist

- [x] iTunes API integration (search + lookup)
- [x] Database schema with all required fields
- [x] REST API with CRUD operations
- [x] JWT Authentication (register + login)
- [x] Centralized error handling
- [x] Request validation
- [x] Search page with debounced search (300ms)
- [x] Library page with pagination
- [x] Edit album (rating + notes)
- [x] Delete album with confirmation
- [x] Analytics dashboard (5 chart types)
- [x] AI Recommendations (Gemini + fallback)
- [x] Responsive UI
- [x] Loading states
- [x] Empty states
- [x] Toast notifications
- [x] Caching (Caffeine)

---

## 📁 Project Structure

```
project/
├── backend/                    # Spring Boot API
│   ├── src/main/java/com/musiccatalog/
│   │   ├── controller/         # REST Controllers
│   │   ├── model/              # JPA Entities
│   │   ├── dto/                # Request/Response DTOs
│   │   ├── repository/         # Data Access Layer
│   │   ├── service/            # Business Logic
│   │   ├── security/           # JWT + Spring Security
│   │   └── exception/          # Global Error Handling
│   └── src/main/resources/
│       └── application.properties
├── frontend/                   # Next.js App
│   ├── src/app/                # Pages (App Router)
│   │   ├── auth/               # Login/Register
│   │   ├── search/             # Album Search
│   │   ├── library/            # Personal Library
│   │   ├── analytics/          # Charts Dashboard
│   │   └── insights/           # AI Recommendations
│   ├── src/components/         # Reusable Components
│   └── src/lib/                # API Client, Auth, Toast
└── README.md
```
