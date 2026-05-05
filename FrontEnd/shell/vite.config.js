import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import federation from '@originjs/vite-plugin-federation';

export default defineConfig({
  plugins: [
    react(),
    federation({
      name: 'shell',
      remotes: {
        productMfe: 'http://localhost:3001/assets/remoteEntry.js',
        searchMfe: 'http://localhost:3002/assets/remoteEntry.js',
        cartMfe: 'http://localhost:3003/assets/remoteEntry.js',
        orderMfe: 'http://localhost:3004/assets/remoteEntry.js',
        adminMfe: 'http://localhost:3005/assets/remoteEntry.js',
      },
      shared: {
        react: { singleton: true, requiredVersion: false },
        'react-dom': { singleton: true, requiredVersion: false },
        'react-router-dom': { singleton: true, requiredVersion: false },
      },
    }),
  ],
  server: { port: 3000 },
  build: { target: 'esnext', minify: false },
});