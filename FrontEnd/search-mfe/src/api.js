const API = 'http://localhost:8080/api';

export async function searchProducts(params) {
  const url = `${API}/search/products/filter?${params}`;
  const res = await fetch(url);
  if (!res.ok) throw new Error('Search failed');
  const data = await res.json();
  return Array.isArray(data) ? data : data.content ?? [];
}

export async function fetchSuggestions(query) {
  const res = await fetch(`${API}/search/suggest?q=${encodeURIComponent(query.trim())}`);
  if (!res.ok) return [];
  const data = await res.json();
  return Array.isArray(data) ? data : [];
}

export async function fetchRecommendations(size = 8) {
  const res = await fetch(`${API}/search/recommendations?size=${size}`);
  if (!res.ok) return [];
  const data = await res.json();
  return Array.isArray(data) ? data : [];
}

export async function fetchCategories() {
  const res = await fetch(`${API}/categories`);
  if (!res.ok) return [];
  const data = await res.json();
  return Array.isArray(data) ? data : [];
}