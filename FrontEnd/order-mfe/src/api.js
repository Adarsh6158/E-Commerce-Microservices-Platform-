const API = 'http://localhost:8080/api';

function authHeaders() {
  const token = localStorage.getItem('access_token');
  const h = { 'Content-Type': 'application/json' };
  if (token) h['Authorization'] = `Bearer ${token}`;
  return h;
}

export async function fetchOrders() {
  const res = await fetch(`${API}/orders`, { headers: authHeaders() });
  if (!res.ok) throw new Error('Failed to load orders');
  return res.json();
}

export async function fetchOrder(id) {
  const res = await fetch(`${API}/orders/${id}`, { headers: authHeaders() });
  if (!res.ok) throw new Error('Failed to load order');
  return res.json();
}

export async function fetchPayment(orderId) {
  const res = await fetch(`${API}/payments/order/${orderId}`, { headers: authHeaders() });
  if (!res.ok) return null;
  return res.json();
}

export async function fetchNotifications(orderId) {
  const res = await fetch(`${API}/notifications/order/${orderId}`, { headers: authHeaders() });
  if (!res.ok) return [];
  return res.json();
}

export async function cancelOrder(orderId) {
  const res = await fetch(`${API}/orders/${orderId}/cancel`, {
    method: 'PUT',
    headers: authHeaders()
  });
  if (!res.ok) throw new Error('Cannot cancel this order');
  return res.json();
}

export async function downloadInvoice(orderId) {
  const res = await fetch(`${API}/orders/${orderId}/invoice`, {
    headers: authHeaders()
  });
  if (!res.ok) throw new Error('Failed to generate invoice');

  const blob = await res.blob();
  const url = URL.createObjectURL(blob);

  const a = document.createElement('a');
  a.href = url;
  a.download = `invoice-${orderId.substring(0, 8)}.pdf`;

  document.body.appendChild(a);
  a.click();

  document.body.removeChild(a);
  URL.revokeObjectURL(url);
}