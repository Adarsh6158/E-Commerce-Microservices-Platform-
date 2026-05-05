import React from 'react';
import { STATUS_COLORS } from './StatusTimeline';

export function OrderList({ orders, onSelectOrder }) {
  return (
    <div>
      <div className="orders-header">
        <h2 className="orders-header__title">My Orders</h2>
      </div>

      {orders.length === 0 ? (
        <p className="orders-empty">No orders yet.</p>
      ) : (
        <div className="order-list" role="list" aria-label="Your orders">
          {orders.map(o => (
            <div
              key={o.id}
              role="listitem"
              onClick={() => onSelectOrder(o)}
              className="order-card"
              tabIndex={0}
              onKeyDown={(e) => {
                if (e.key === 'Enter') onSelectOrder(o);
              }}
            >
              <div className="order-card__header">
                <span className="order-card__id">#{o.id.substring(0, 8)}</span>
                <span
                  className="badge"
                  style={{ background: STATUS_COLORS[o.status] || '#888' }}
                >
                  {o.status}
                </span>
              </div>

              <div className="order-card__meta">
                ${o.totalAmount} · {new Date(o.createdAt).toLocaleDateString()}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}