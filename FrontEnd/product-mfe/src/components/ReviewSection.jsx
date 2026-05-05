import React, { useState } from 'react';
import { StarRating } from './StarRating';

export function ReviewSection({ productId, reviews, rating, onSubmit }) {
  const [showForm, setShowForm] = useState(false);
  const [newRating, setNewRating] = useState(0);
  const [title, setTitle] = useState('');
  const [comment, setComment] = useState('');
  const [submitting, setSubmitting] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (newRating === 0 || !title.trim()) return;
    setSubmitting(true);
    await onSubmit(productId, { rating: newRating, title: title.trim(), comment: comment.trim() });
    setNewRating(0);
    setTitle('');
    setComment('');
    setShowForm(false);
    setSubmitting(false);
  };

  const avgRating = rating?.averageRating ?? 0;
  const reviewCount = rating?.reviewCount ?? 0;

  return (
    <section className="reviews-section" aria-label="Customer Reviews">
      <div className="reviews-header">
        <div className="reviews-header__left">
          <h3 className="reviews-header__title">Customer Reviews</h3>
          <div className="reviews-header__summary">
            <StarRating value={avgRating} />
            <span className="reviews-header__avg">{avgRating > 0 ? avgRating.toFixed(1) : '-'}</span>
            <span className="reviews-header__count">({reviewCount} review{reviewCount !== 1 ? 's' : ''})</span>
          </div>
        </div>
        {!showForm && (
          <button onClick={() => setShowForm(true)} className="reviews-header__write-btn">✏️ Write a Review</button>
        )}
      </div>

      {showForm && (
        <form onSubmit={handleSubmit} className="review-form">
          <div className="review-form__rating">
            <label className="review-form__label">Your Rating</label>
            <StarRating value={newRating} interactive onChange={setNewRating} />
          </div>
          <input
            value={title}
            onChange={e => setTitle(e.target.value)}
            placeholder="Review title"
            className="review-form__input"
            required
          />
          <textarea
            value={comment}
            onChange={e => setComment(e.target.value)}
            placeholder="Share your experience (optional)"
            className="review-form__textarea"
            rows={3}
          />
          <div className="review-form__actions">
            <button type="submit" disabled={submitting || newRating === 0 || !title.trim()} className="review-form__submit">
              {submitting ? 'Submitting...' : 'Submit Review'}
            </button>
            <button type="button" onClick={() => setShowForm(false)} className="review-form__cancel">Cancel</button>
          </div>
        </form>
      )}

      {reviews.length === 0 ? (
        <p className="reviews-empty">No reviews yet. Be the first to share your thoughts!</p>
      ) : (
        <div className="reviews-list">
          {reviews.map(r => (
            <div key={r.id} className="review-card">
              <div className="review-card__header">
                <div className="review-card__avatar">{(r.userName || 'A')[0].toUpperCase()}</div>
                <div className="review-card__meta">
                  <span className="review-card__author">{r.userName || 'Anonymous'}</span>
                  <span className="review-card__date">{new Date(r.createdAt).toLocaleDateString()}</span>
                </div>
                <StarRating value={r.rating} />
              </div>
              <h4 className="review-card__title">{r.title}</h4>
              {r.comment && <p className="review-card__comment">{r.comment}</p>}
              {r.verified && <span className="review-card__verified">✓ Verified Purchase</span>}
            </div>
          ))}
        </div>
      )}
    </section>
  );
}
