import React from 'react';
import { cardGradient } from '../utils/gradients';

export function CartItem({ item, isUpdating, onUpdateQty, onRemove }) {
  return (
    <div className={`cart-item${isUpdating ? ' cart-item--updating' : ''}`}>
      <div
        className="cart-item__image"
        style={{ background: cardGradient(item.productId) }}
      >
        <span className="cart-item__image-icon">
          {item.name?.[0]?.toUpperCase() || '?'}
        </span>
      </div>

      <div className="cart-item__info">
        <h4 className="cart-item__name">{item.name}</h4>
        <p className="cart-item__price-each">
          ${Number(item.unitPrice).toFixed(2)} each
        </p>
      </div>

      <div className="qty-controls">
        <button
          onClick={() =>
            item.quantity > 1 &&
            onUpdateQty(item.productId, item.quantity - 1)
          }
          disabled={isUpdating || item.quantity <= 1}
          className="qty-btn"
          aria-label="Decrease quantity"
        >
          −
        </button>

        <span
          className="qty-value"
          aria-label={`Quantity: ${item.quantity}`}
        >
          {item.quantity}
        </span>

        <button
          onClick={() =>
            onUpdateQty(item.productId, item.quantity + 1)
          }
          disabled={isUpdating}
          className="qty-btn"
          aria-label="Increase quantity"
        >
          +
        </button>
      </div>

      <div className="cart-item__total">
        <p className="cart-item__total-price">
          ${(item.unitPrice * item.quantity).toFixed(2)}
        </p>
        <button
          onClick={() => onRemove(item.productId, item.name)}
          disabled={isUpdating}
          className="btn-remove"
        >
          Remove
        </button>
      </div>
    </div>
  );
}