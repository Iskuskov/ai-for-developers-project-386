// Запуск e2e в «мягком» режиме: если браузеры Playwright не установлены,
// сценарий пропускается с кодом 0 вместо падения.
// Обязательный запуск — mise run check:e2e (входит в mise run check).
import { spawnSync } from 'node:child_process'
import { existsSync } from 'node:fs'
import { chromium } from '@playwright/test'

if (!existsSync(chromium.executablePath())) {
  console.log(
    'Браузеры Playwright не установлены — e2e пропущен (установить: mise run install:playwright)',
  )
  process.exit(0)
}

const result = spawnSync('pnpm', ['exec', 'playwright', 'test'], {
  stdio: 'inherit',
  shell: true,
})
process.exit(result.status ?? 1)
