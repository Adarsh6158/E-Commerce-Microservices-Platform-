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

export async function checkInventory(productId) {
  const res = await fetch(`${API}/inventory/${productId}`, { headers: authHeaders() });
  if (!res.ok) throw new Error('Not found');
  return res.json();
}

export async function addStock({ productId, sku, quantity, warehouseId = 'WH-01' }) {
  const res = await fetch(`${API}/inventory/stock`, {
    method: 'POST',
    headers: authHeaders(),
    body: JSON.stringify({ productId, sku, quantity, warehouseId }),
  });
  if (!res.ok) throw new Error('Failed to add stock');
  return res.json();
}

export async function fetchAnalytics() {
  const res = await fetch(`${API}/analytics/realtime`, { headers: authHeaders() });
  if (!res.ok) return null;
  return res.json();
}

export async function createProduct(productData) {
  const res = await fetch(`${API}/products`, {
    method: 'POST',
    headers: authHeaders(),
    body: JSON.stringify({ ...productData, active: true }),
  });
  if (!res.ok) {
    const b = await res.json().catch(() => ({}));
    throw new Error(b.message || 'Failed to create product');
  }
  return res.json();
}

export async function uploadProductImages(productId, files) {
  const token = localStorage.getItem('access_token');
  const formData = new FormData();
  files.forEach(f => formData.append('files', f));

  const res = await fetch(`${API}/products/${productId}/images`, {
    method: 'POST',
    headers: { Authorization: `Bearer ${token}` },
    body: formData,
  });

  if (!res.ok) throw new Error('Image upload failed');
  return res.json();
}