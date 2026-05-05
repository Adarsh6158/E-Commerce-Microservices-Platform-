import React, { Component } from 'react';
import './ErrorBoundary.css';

export class ErrorBoundary extends Component {
  state = { error: null };

  static getDerivedStateFromError(error) {
    return { error };
  }

  componentDidCatch(error, info) {
    console.error('[MFE Error]', error, info.componentStack);
  }

  render() {
    if (this.state.error) {
      return (
        <div className="error-boundary">
          <h3>Something went wrong loading this section</h3>
          <p>{this.state.error.message}</p>
          <button
            onClick={() => this.setState({ error: null })}
            className="error-boundary__retry"
          >
            Retry
          </button>
        </div>
      );
    }

    return this.props.children;
  }
}