import React from 'react';
import { StatusTimeline, STATUS_COLORS } from './StatusTimeline';

export function OrderDetail({ order, payment, notifications, onBack, onRefresh, onCancel, onDownloadInvoice }) {
  return (
    <div>
      <button onClick={onBack} className="btn-back">← Back to Orders</button>

      <div className="order-detail">
        <div className="order-detail__header">
          <h3 className="order-detail__title">Order #{order.id?.substring(0, 8)}</h3>
          <span
            className="badge"
            style={{ background: STATUS_COLORS[order.status] || '#888' }}
          >
            {order.status}
          </span>
        </div>

        <p className="order-detail__date">
          Created: {new Date(order.createdAt).toLocaleString()}
        </p>

        <StatusTimeline status={order.status} />

        <p className="order-detail__total">
          <strong>Total:</strong> ${order.totalAmount}
        </p>

        {order.correlationId && (
          <p className="order-detail__correlation">
            Correlation: {order.correlationId}
          </p>
        )}

        {order.failureReason && (
          <p className="order-detail__failure">{order.failureReason}</p>
        )}

        <div className="order-detail__actions">
          <button onClick={onRefresh} className="btn-back">
            Refresh Status
          </button>

          {order.status === 'CONFIRMED' && (
            <button
              onClick={() => onDownloadInvoice(order.id)}
              className="btn-back btn-back--invoice"
            >
              📄 Download Invoice
            </button>
          )}

          {(order.status === 'PENDING' || order.status === 'INVENTORY_RESERVED') && (
            <button
              onClick={() => onCancel(order.id)}
              className="btn-back btn-back--danger"
            >
              Cancel Order
            </button>
          )}
        </div>

        {order.items?.length > 0 && (
          <div className="order-items">
            <h4 className="order-items__title">Items</h4>
            <table className="order-items__table">
              <thead>
                <tr>
                  <th>Product</th>
                  <th>Qty</th>
                  <th>Price</th>
                  <th>Subtotal</th>
                </tr>
              </thead>
              <tbody>
                {order.items.map((item, i) => (
                  <tr key={i}>
                    <td>{item.productName || item.name || '-'}</td>
                    <td>{item.quantity}</td>
                    <td>${Number(item.unitPrice).toFixed(2)}</td>
                    <td>${(item.quantity * item.unitPrice).toFixed(2)}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}

        {payment && (
          <div className="order-payment">
            <h4 className="order-payment__title">Payment</h4>
            <p>Status: <strong>{payment.status}</strong></p>
            <p>Transaction: {payment.transactionRef}</p>
            <p>Amount: ${payment.amount}</p>
          </div>
        )}

        {notifications.length > 0 && (
          <div className="order-notifications">
            <h4 className="order-notifications__title">Notifications</h4>
            {notifications.map((n, i) => (
              <div key={n.id ?? i} className="order-notification">
                [{n.type}] {n.subject} – <em>{n.status}</em>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}