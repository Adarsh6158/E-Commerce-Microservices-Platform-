const API = 'http://localhost:8080/api';

function authHeaders() {
  const token = localStorage.getItem('access_token');
  const h = { 'Content-Type': 'application/json' };
  if (token) h['Authorization'] = `Bearer ${token}`;
  return h;
}

export async function fetchProducts() {
  const res = await fetch(`${API}/products`, { headers: authHeaders() });
  if (!res.ok) throw new Error('Failed to load products');
  return res.json();
}

export async function fetchProduct(id) {
  const res = await fetch(`${API}/products/${id}`, { headers: authHeaders() });
  if (!res.ok) throw new Error('Product not found');
  return res.json();
}

export async function fetchCategories() {
  const res = await fetch(`${API}/categories`, { headers: authHeaders() });
  if (!res.ok) throw new Error('Failed to load categories');
  return res.json();
}

export async function fetchPrice(productId, quantity = 1, basePrice) {
  try {
    const params = new URLSearchParams({ productId, quantity: String(quantity) });
    if (basePrice != null) params.set('basePrice', String(basePrice));
    const res = await fetch(`${API}/pricing/calculate?${params}`, { headers: authHeaders() });
    if (!res.ok) return null;
    return res.json();
  } catch {
    return null;
  }
}

export async function fetchReviews(productId) {
  const res = await fetch(`${API}/products/${productId}/reviews`, { headers: authHeaders() });
  if (!res.ok) return [];
  return res.json();
}

export async function fetchRating(productId) {
  const res = await fetch(`${API}/products/${productId}/reviews/rating`, { headers: authHeaders() });
  if (!res.ok) return { averageRating: 0, reviewCount: 0 };
  return res.json();
}

export async function submitReview(productId, review) {
  const res = await fetch(`${API}/products/${productId}/reviews`, {
    method: 'POST',
    headers: authHeaders(),
    body: JSON.stringify(review),
  });
  if (!res.ok) {
    const text = await res.text();
    throw new Error(text || 'Failed to submit review');
  }
  return res.json();
}

export async function addToCart(product) {
  const token = localStorage.getItem('access_token');
  if (!token) throw new Error('Please log in to add items to cart');

  const res = await fetch(`${API}/cart/items`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${token}` },
    body: JSON.stringify({
      productId: product.id,
      sku: product.sku,
      name: product.name,
      quantity: 1,
      unitPrice: product.basePrice,
    }),
  });

  if (!res.ok) throw new Error('Failed to add to cart');
  return res.json();
}