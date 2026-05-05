import React, { Suspense, lazy, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { ErrorBoundary } from '../components/ErrorBoundary';
import { onMfeEvent, MfeEvents, emitMfeEvent } from '../lib/events';
import { useAuth } from '../lib/auth';

const CartApp = lazy(() => import('cartMfe/App'));

function LoadingFallback() {
  return (
    <div className="cart-centered">
      <div className="spinner"></div>
      <p className="cart-centered__text" style={{ marginTop: 16 }}>
        Loading cart…
      </p>
    </div>
  );
}

export default function CartPage() {
  const navigate = useNavigate();
  const { user, isAuthenticated } = useAuth();

  useEffect(() => {
    emitMfeEvent(MfeEvents.AUTH_CHANGED, { user, isAuthenticated });
  }, [user, isAuthenticated]);

  useEffect(() => {
    return onMfeEvent(MfeEvents.ORDER_PLACED, ({ orderId }) => {
      emitMfeEvent(MfeEvents.CART_UPDATED);
      if (orderId) navigate('/orders');
    });
  }, [navigate]);

  useEffect(() => {
    return onMfeEvent(MfeEvents.NAVIGATE, ({ path }) => {
      if (path) navigate(path);
    });
  }, [navigate]);

  return (
    <ErrorBoundary>
      <Suspense fallback={<LoadingFallback />}>
        <CartApp />
      </Suspense>
    </ErrorBoundary>
  );
}