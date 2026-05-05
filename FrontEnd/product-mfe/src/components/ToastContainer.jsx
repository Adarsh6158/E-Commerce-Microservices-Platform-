import React from 'react';

export function ToastContainer({ toasts, onDismiss }) {
  if (!toasts.length) return null;
  return (
    <div className="toast-container">
      {toasts.map(t => (
        <div key={t.id} className={`toast toast--${t.type}`} role="alert">
          <span>{t.type === 'success' ? '✅' : '❌'}</span>
          <span style={{ flex: 1 }}>{t.message}</span>
          <button onClick={() => onDismiss(t.id)} className="toast__close" aria-label="Dismiss">x</button>
        </div>
      ))}
    </div>
  );
}
