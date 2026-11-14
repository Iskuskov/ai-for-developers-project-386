# React + TypeScript + Vite

This template provides a minimal setup to get React working in Vite with HMR and some Oxlint rules.

## E2E (Playwright)

Один сквозной «дымовой» сценарий: `frontend/e2e/smoke.spec.ts` — гость записывается на свободный слот,
владелец видит Запись в списке. Стек поднимает сам Playwright (`webServer` в
`playwright.config.ts`): бекенд через `./gradlew bootRun` (:8080) и собранный фронтенд через
`vite preview` (:4173, с прокси API на бекенд).

```sh
pnpm install                              # зависимости, включая @playwright/test
pnpm exec playwright install chromium     # браузер для e2e (один раз)
pnpm exec playwright test                 # при уже собранном dist (pnpm build)
```

Через mise из корня репозитория: `mise run install:playwright` (браузер), `mise run check:e2e`
(сборка фронтенда + стек + сценарий), `mise run check:e2e:optional` (то же, но пропуск, если
браузер не установлен). В `mise run check` e2e выполняется всегда.

Currently, two official plugins are available:

- [@vitejs/plugin-react](https://github.com/vitejs/vite-plugin-react/blob/main/packages/plugin-react) uses [Oxc](https://oxc.rs)
- [@vitejs/plugin-react-swc](https://github.com/vitejs/vite-plugin-react/blob/main/packages/plugin-react-swc) uses [SWC](https://swc.rs/)

## React Compiler

The React Compiler is not enabled on this template because of its impact on dev & build performances. To add it, see [this documentation](https://react.dev/learn/react-compiler/installation).

## Expanding the Oxlint configuration

If you are developing a production application, we recommend enabling type-aware lint rules by installing `oxlint-tsgolint` and editing `.oxlintrc.json`:

```json
{
  "$schema": "./node_modules/oxlint/configuration_schema.json",
  "plugins": ["react", "typescript", "oxc"],
  "options": {
    "typeAware": true
  },
  "rules": {
    "react/rules-of-hooks": "error",
    "react/only-export-components": ["warn", { "allowConstantExport": true }]
  }
}
```

See the [Oxlint rules documentation](https://oxc.rs/docs/guide/usage/linter/rules) for the full list of rules and categories.
