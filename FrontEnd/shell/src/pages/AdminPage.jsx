import React, { Suspense, lazy, useEffect } from 'react';
import { ErrorBoundary } from '../components/ErrorBoundary';
import { emitMfeEvent, MfeEvents } from '../lib/events';
import { useAuth } from '../lib/auth';

const AdminApp = lazy(() => import('adminMfe/App'));

function LoadingFallback() {
  return <p className="panel-loading">Loading admin…</p>;
}

export default function AdminPage() {
  const { user, isAuthenticated } = useAuth();

  useEffect(() => {
    emitMfeEvent(MfeEvents.AUTH_CHANGED, { user, isAuthenticated });
  }, [user, isAuthenticated]);

  if (!user?.roles?.includes('ADMIN')) {
    return (
      <div className="cart-centered">
        <div className="cart-centered__icon">🔒</div>
        <h2 className="cart-centered__title">Access Denied</h2>
        <p className="cart-centered__text">Admin privileges required.</p>
      </div>
    );
  }

  return (
    <ErrorBoundary>
      <Suspense fallback={<LoadingFallback />}>
        <AdminApp />
      </Suspense>
    </ErrorBoundary>
  );
}