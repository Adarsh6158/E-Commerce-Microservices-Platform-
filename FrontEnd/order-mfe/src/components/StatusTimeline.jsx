import React from 'react';

const STATUS_COLORS = {
  PENDING: '#f39c12',
  INVENTORY_RESERVED: '#2980b9',
  PAYMENT_PROCESSING: '#8e44ad',
  CONFIRMED: '#27ae60',
  CANCELLED: '#95a5a6',
  FAILED: '#c0392b',
};

const SAGA_STEPS = [
  { key: 'PENDING', label: 'Order Placed', icon: '📝' },
  { key: 'INVENTORY_RESERVED', label: 'Inventory Reserved', icon: '📦' },
  { key: 'PAYMENT_PROCESSING', label: 'Payment Processing', icon: '💳' },
  { key: 'CONFIRMED', label: 'Confirmed', icon: '✅' },
];

function sagaIndex(status) {
  const idx = SAGA_STEPS.findIndex(s => s.key === status);
  return idx >= 0 ? idx : -1;
}

export { STATUS_COLORS };

export function StatusTimeline({ status }) {
  const isFailed = status === 'FAILED' || status === 'CANCELLED';
  const currentIdx = isFailed ? -1 : sagaIndex(status);

  return (
    <div className="timeline" role="list" aria-label="Order status timeline">
      {SAGA_STEPS.map((step, i) => {
        const done = !isFailed && i <= currentIdx;
        const active = !isFailed && i === currentIdx;

        return (
          <div
            key={step.key}
            role="listitem"
            className={`timeline__step${done ? ' timeline__step--done' : ''}${active ? ' timeline__step--active' : ''}`}
          >
            <div
              className="timeline__icon"
              style={done ? { background: STATUS_COLORS[step.key] || '#27ae60', color: '#fff' } : {}}
            >
              {step.icon}
            </div>

            {i < SAGA_STEPS.length - 1 && (
              <div
                className={`timeline__line${done && i < currentIdx ? ' timeline__line--done' : ''}`}
              />
            )}

            <span className="timeline__label">{step.label}</span>
          </div>
        );
      })}

      {isFailed && (
        <div
          role="listitem"
          className="timeline__step timeline__step--done timeline__step--active"
        >
          <div
            className="timeline__icon"
            style={{ background: STATUS_COLORS[status], color: '#fff' }}
          >
            {status === 'CANCELLED' ? '🚫' : '❌'}
          </div>
          <span className="timeline__label">{status}</span>
        </div>
      )}
    </div>
  );
}