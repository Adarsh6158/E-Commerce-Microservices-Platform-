import React, { useEffect, useState, useCallback } from 'react';
import { fetchCart, updateQuantity, removeCartItem, placeOrder, clearCart } from './api';
import { CartItem } from './components/CartItem';
import { OrderSummary } from './components/OrderSummary';
import './App.css';

function useToast() {
  const [toasts, setToasts] = useState([]);

  const show = useCallback((message, type = 'success') => {
    const id = Date.now();
    setToasts(prev => [...prev, { id, message, type }]);
    setTimeout(() => setToasts(prev => prev.filter(t => t.id !== id)), 4000);
  }, []);

  const dismiss = useCallback((id) => {
    setToasts(prev => prev.filter(t => t.id !== id));
  }, []);

  return { toasts, show, dismiss };
}

function ToastContainer({ toasts, onDismiss }) {
  return (
    <div className="toast-container" role="status" aria-live="polite">
      {toasts.map(t => (
        <div key={t.id} className={`toast toast--${t.type}`}>
          <span>{t.type === 'success' ? '✓' : '✗'}</span>
          <span style={{ flex: 1 }}>{t.message}</span>
          <button onClick={() => onDismiss(t.id)} className="toast__close" aria-label="Dismiss">×</button>
        </div>
      ))}
    </div>
  );
}

export default function App() {
  const [cart, setCart] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [placingOrder, setPlacingOrder] = useState(false);
  const [updatingItem, setUpdatingItem] = useState(null);
  const { toasts, show: showToast, dismiss: dismissToast } = useToast();

  const loadCart = async () => {
    try {
      setCart(await fetchCart());
    } catch (e) {
      setError(e.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadCart();
  }, []);

  const handleUpdateQty = async (productId, quantity) => {
    setUpdatingItem(productId);
    try {
      await updateQuantity(productId, quantity);
      await loadCart();
    } finally {
      setUpdatingItem(null);
    }
  };

  const handleRemoveItem = async (productId, name) => {
    setUpdatingItem(productId);
    try {
      await removeCartItem(productId);
      showToast(`${name} removed from cart`);
      await loadCart();
      window.dispatchEvent(new CustomEvent('mfe:cart:updated'));
    } finally {
      setUpdatingItem(null);
    }
  };

  const handlePlaceOrder = async () => {
    if (!cart?.items?.length) return;
    setPlacingOrder(true);

    try {
      const order = await placeOrder(cart.items);
      await clearCart();
      showToast(`Order placed successfully! ID: ${order.id}`);

      window.dispatchEvent(new CustomEvent('mfe:order:placed', {
        detail: { orderId: order.id }
      }));

      window.dispatchEvent(new CustomEvent('mfe:cart:updated'));
      loadCart();
    } catch (e) {
      showToast(e.message, 'error');
    } finally {
      setPlacingOrder(false);
    }
  };

  if (loading) return (
    <div className="cart-centered">
      <div className="spinner"></div>
      <p className="cart-centered__text" style={{ marginTop: 16 }}>
        Loading your cart...
      </p>
    </div>
  );

  if (error === 'LOGIN_REQUIRED') return (
    <div className="cart-centered">
      <div className="cart-centered__icon">🔒</div>
      <h2 className="cart-centered__title">Sign in required</h2>
      <p className="cart-centered__text">
        Please sign in to view your shopping cart.
      </p>
      <a href="/login" className="link-signin">Sign in →</a>
    </div>
  );

  if (error) return (
    <div className="cart-centered">
      <div className="cart-centered__icon">⚠️</div>
      <p style={{ color: '#c0392b', fontSize: 15 }}>{error}</p>
      <button
        onClick={() => {
          setError('');
          setLoading(true);
          loadCart();
        }}
        className="btn-retry"
      >
        Try again
      </button>
    </div>
  );

  const items = cart?.items || [];
  const total = cart?.total ?? items.reduce((s, i) => s + i.unitPrice * i.quantity, 0);

  if (items.length === 0) return (
    <div>
      <ToastContainer toasts={toasts} onDismiss={dismissToast} />
      <div className="empty-cart">
        <div className="empty-cart__icon">🛒</div>
        <h2 className="empty-cart__title">Your cart is empty</h2>
        <p className="empty-cart__text">
          Looks like you haven't added any items yet.
        </p>
        <a href="/" className="btn-continue">Continue Shopping →</a>
      </div>
    </div>
  );

  return (
    <div>
      <ToastContainer toasts={toasts} onDismiss={dismissToast} />

      <div className="cart-header">
        <div>
          <h2 className="cart-header__title">Shopping Cart</h2>
          <p className="cart-header__count">
            {items.length} item{items.length !== 1 ? 's' : ''}
          </p>
        </div>
      </div>

      <div className="cart-layout">
        <div className="cart-items" role="list" aria-label="Cart items">
          {items.map((item, i) => (
            <CartItem
              key={item.productId ?? i}
              item={item}
              isUpdating={updatingItem === item.productId}
              onUpdateQty={handleUpdateQty}
              onRemove={handleRemoveItem}
            />
          ))}
        </div>

        <OrderSummary
          total={total}
          onPlaceOrder={handlePlaceOrder}
          placingOrder={placingOrder}
        />
      </div>
    </div>
  );
}