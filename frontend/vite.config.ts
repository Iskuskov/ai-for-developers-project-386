import react from '@vitejs/plugin-react'
import { defineConfig } from 'vitest/config'

// API-запросы ходят на тот же origin, поэтому все руты бекенда проксируются:
// в dev (vite) и на собранной сборке (vite preview, её поднимает e2e).
const apiProxy = {
  '/event-types': 'http://localhost:8080',
  '/slots': 'http://localhost:8080',
  '/bookings': 'http://localhost:8080',
}

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    // В dev API-запросы ходят на бекенд-демон (mise daemons start)
    proxy: apiProxy,
  },
  preview: {
    // На собранной сборке (pnpm preview) прокси нужен для e2e-сценария
    proxy: apiProxy,
  },
  test: {
    globals: true,
    environment: 'jsdom',
    setupFiles: './vitest.setup.mjs',
    include: ['src/**/*.test.{ts,tsx}'],
  },
})
