import React, { useEffect, useState } from 'react';
import { fetchAnalytics } from '../api';

export function AnalyticsPanel() {
  const [summary, setSummary] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchAnalytics()
      .then(setSummary)
      .catch(() => {})
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <p className="panel-loading">Loading analytics…</p>;

  return (
    <div>
      <h3 className="panel-title">Real-Time Analytics (Last Hour)</h3>
      {summary ? (
        <div className="analytics-grid">
          {Object.entries(summary).map(([key, val]) => (
            <div key={key} className="analytics-card">
              <div className="analytics-card__value">{String(val)}</div>
              <div className="analytics-card__label">
                {key.replace(/([A-Z])/g, ' $1')}
              </div>
            </div>
          ))}
        </div>
      ) : (
        <p className="analytics-empty">
          No analytics data available yet. Process some orders first.
        </p>
      )}
    </div>
  );
}