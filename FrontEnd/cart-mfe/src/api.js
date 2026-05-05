const API = 'http://localhost:8080/api';

function authHeaders() {
  const token = localStorage.getItem('access_token');
  const h = { 'Content-Type': 'application/json' };
  if (token) h['Authorization'] = `Bearer ${token}`;
  return h;
}

export async function fetchCart() {
  const token = localStorage.getItem('access_token');
  if (!token) throw new Error('LOGIN_REQUIRED');

  const res = await fetch(`${API}/cart`, { headers: authHeaders() });

  if (res.status === 401) throw new Error('LOGIN_REQUIRED');
  if (!res.ok) throw new Error('Failed to load cart');

  return res.json();
}

export async function updateQuantity(productId, quantity) {
  await fetch(`${API}/cart/items/${productId}?quantity=${quantity}`, {
    method: 'PUT',
    headers: authHeaders(),
  });
}

export async function removeCartItem(productId) {
  await fetch(`${API}/cart/items/${productId}`, {
    method: 'DELETE',
    headers: authHeaders(),
  });
}

export async function placeOrder(items) {
  const orderItems = items.map((item) => ({
    productId: item.productId,
    sku: item.sku || 'N/A',
    productName: item.name,
    quantity: item.quantity,
    unitPrice: item.unitPrice,
  }));

  const res = await fetch(`${API}/orders`, {
    method: 'POST',
    headers: authHeaders(),
    body: JSON.stringify({ items: orderItems }),
  });

  if (!res.ok) {
    const b = await res.json().catch(() => ({}));
    throw new Error(b.message || 'Order failed');
  }

  return res.json();
}

export async function clearCart() {
  await fetch(`${API}/cart`, {
    method: 'DELETE',
    headers: authHeaders(),
  });
}