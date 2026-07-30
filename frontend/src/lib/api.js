import axios from 'axios';

const API_BASE = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';

const api = axios.create({
  baseURL: API_BASE,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor - add JWT token
api.interceptors.request.use((config) => {
  if (typeof window !== 'undefined') {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
  }
  return config;
});

// Response interceptor - handle auth errors
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      if (typeof window !== 'undefined') {
        localStorage.removeItem('token');
        localStorage.removeItem('username');
        window.location.href = '/auth';
      }
    }
    return Promise.reject(error);
  }
);

// Auth API
export const authApi = {
  register: (data) => api.post('/api/auth/register', data),
  login: (data) => api.post('/api/auth/login', data),
};

// Search API
export const searchApi = {
  search: (query, limit = 25) =>
    api.get(`/api/search?query=${encodeURIComponent(query)}&limit=${limit}`),
  lookup: (id) => api.get(`/api/search/lookup/${id}`),
};

// Library API
export const libraryApi = {
  getAll: (page = 0, size = 20, sortBy = 'createdAt', direction = 'desc') =>
    api.get(`/api/library?page=${page}&size=${size}&sortBy=${sortBy}&direction=${direction}`),
  add: (album) => api.post('/api/library', album),
  update: (id, data) => api.put(`/api/library/${id}`, data),
  delete: (id) => api.delete(`/api/library/${id}`),
  get: (id) => api.get(`/api/library/${id}`),
};

// Analytics API
export const analyticsApi = {
  get: () => api.get('/api/analytics'),
};

// AI API
export const aiApi = {
  getRecommendations: () => api.get('/api/ai/recommendations'),
};

export default api;
