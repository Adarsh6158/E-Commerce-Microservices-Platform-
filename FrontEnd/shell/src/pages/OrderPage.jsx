import React, { Suspense, lazy, useEffect } from 'react';
import { ErrorBoundary } from '../components/ErrorBoundary';
import { emitMfeEvent, MfeEvents } from '../lib/events';
import { useAuth } from '../lib/auth';

const OrderApp = lazy(() => import('orderMfe/App'));

function LoadingFallback() {
  return <p className="orders-loading">Loading orders…</p>;
}

export default function OrderPage() {
  const { user, isAuthenticated } = useAuth();

  useEffect(() => {
    emitMfeEvent(MfeEvents.AUTH_CHANGED, { user, isAuthenticated });
  }, [user, isAuthenticated]);

  return (
    <ErrorBoundary>
      <Suspense fallback={<LoadingFallback />}>
        <OrderApp />
      </Suspense>
    </ErrorBoundary>
  );
}