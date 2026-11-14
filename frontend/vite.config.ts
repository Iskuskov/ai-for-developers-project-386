import react from '@vitejs/plugin-react'
import { defineConfig } from 'vitest/config'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    // В dev API-запросы ходят на бекенд-демон (mise daemons start)
    proxy: {
      '/event-types': 'http://localhost:8080',
      '/slots': 'http://localhost:8080',
    },
  },
  test: {
    globals: true,
    environment: 'jsdom',
    setupFiles: './vitest.setup.mjs',
  },
})
