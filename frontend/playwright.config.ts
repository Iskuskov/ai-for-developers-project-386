import { defineConfig } from '@playwright/test'

/**
 * Сквозной сценарий живёт на живом стеке:
 *  - бекенд: Gradle bootRun на http://localhost:8080 (готов, когда отвечает GET /event-types);
 *  - фронтенд: vite preview собранной сборки (build:frontend) на http://localhost:4173,
 *    API-запросы проксируются на бекенд (см. preview.proxy в vite.config.ts).
 *
 * Запуск: mise run check:e2e (браузеры: mise run install:playwright).
 */
export default defineConfig({
  testDir: './e2e',
  // Один сценарий, работающий с общим состоянием стека, — без параллельности
  fullyParallel: false,
  workers: 1,
  timeout: 60_000,
  expect: { timeout: 10_000 },
  retries: process.env.CI ? 1 : 0,
  use: {
    baseURL: 'http://localhost:4173',
    trace: 'retain-on-failure',
  },
  webServer: [
    {
      // exec — чтобы SIGTERM от Playwright завершал именно сервер, а не оболочку-родителя
      command: 'cd ../backend && exec ./gradlew bootRun --console=plain',
      url: 'http://localhost:8080/event-types',
      reuseExistingServer: !process.env.CI,
      timeout: 180_000,
    },
    {
      command: 'exec ./node_modules/.bin/vite preview',
      url: 'http://localhost:4173',
      reuseExistingServer: !process.env.CI,
      timeout: 60_000,
    },
  ],
})
