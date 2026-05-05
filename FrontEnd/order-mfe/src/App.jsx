import React, { useEffect, useState } from 'react';
import { fetchOrders, fetchOrder, fetchPayment, fetchNotifications, cancelOrder as cancelOrderApi, downloadInvoice as downloadInvoiceApi } from './api';
import { OrderList } from './components/OrderList';
import { OrderDetail } from './components/OrderDetail';
import './App.css';

export default function App() {
  const [orders, setOrders] = useState([]);
  const [selected, setSelected] = useState(null);
  const [payment, setPayment] = useState(null);
  const [notifications, setNotifications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const loadOrders = async () => {
    try {
      setOrders(await fetchOrders());
    } catch (e) {
      setError(e.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { loadOrders(); }, []);

  const viewOrder = async (order) => {
    setSelected(order);
    const [payRes, notifRes] = await Promise.allSettled([
      fetchPayment(order.id),
      fetchNotifications(order.id),
    ]);
    setPayment(payRes.status === 'fulfilled' ? payRes.value : null);
    setNotifications(notifRes.status === 'fulfilled' ? (notifRes.value || []) : []);
  };

  const handleCancel = async (orderId) => {
    try {
      await cancelOrderApi(orderId);
      setSelected(null);
      loadOrders();
    } catch (e) {
      alert(e.message);
    }
  };

  const handleDownloadInvoice = async (orderId) => {
    try {
      await downloadInvoiceApi(orderId);
    } catch {
      alert('Failed to download invoice');
    }
  };

  const refreshStatus = async () => {
    if (!selected) return;
    try {
      const updated = await fetchOrder(selected.id);
      setSelected(updated);
      loadOrders();
    } catch { /* ignore */ }
  };

  if (loading) return <p className="orders-loading">Loading orders...</p>;
  if (error) return <p className="orders-error">{error}</p>;

  if (selected) {
    return (
      <OrderDetail
        order={selected}
        payment={payment}
        notifications={notifications}
        onBack={() => { setSelected(null); setPayment(null); setNotifications([]); }}
        onRefresh={refreshStatus}
        onCancel={handleCancel}
        onDownloadInvoice={handleDownloadInvoice}
      />
    );
  }

  return <OrderList orders={orders} onSelectOrder={viewOrder} />;
}