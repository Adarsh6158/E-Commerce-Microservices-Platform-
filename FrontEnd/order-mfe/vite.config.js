import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import federation from '@originjs/vite-plugin-federation';

export default defineConfig({
  plugins: [
    react({ jsxRuntime: 'classic' }),
    federation({
      name: 'orderMfe',
      filename: 'remoteEntry.js',
      exposes: { './App': './src/App.jsx' },
      shared: {
        react: { singleton: true, requiredVersion: false },
        'react-dom': { singleton: true, requiredVersion: false },
        'react-router-dom': { singleton: true, requiredVersion: false }
      },
    }),
  ],
  server: { port: 3004 },
  build: { target: 'esnext', minify: false },
});