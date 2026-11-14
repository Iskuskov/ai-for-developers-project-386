# AGENTS.md

## Структура проекта

- `backend/` — Spring Boot 4 (Java 25), Gradle (Kotlin DSL, wrapper в `backend/gradlew`)
- `frontend/` — Vite 8 + React 19 + TypeScript, UI — Mantine, пакетный менеджер pnpm
- `mise.toml` — зафиксированные версии инструментов и все задачи проекта
- `hk.pkl` — git-хуки: pre-commit (форматирование Java, линт фронтенда), commit-msg (формат коммита)

## Команды

Всё запускается через mise из корня репозитория:

- `mise install` — установить инструменты (Java, Node.js, pnpm, hk, google-java-format)
- `mise run install` — установить зависимости бекенда и фронтенда
- `mise run dev:backend` / `mise run dev:frontend` — запуск (порты 8080 и 5173)
- `mise daemons start` / `mise daemons stop` — те же серверы фоном (experimental, pitchfork)
- `mise run test:backend` / `mise run test:frontend` — тесты (JUnit, Vitest)
- `mise run lint:backend` / `mise run lint:frontend` — линтеры (google-java-format, oxlint)
- `mise run format:backend` — автоформат Java-кода
- `mise run check` — все проверки разом: линтеры, тесты, сборка фронтенда, перегенерация контракта и сквозной браузерный сценарий (Playwright) на живом сервере без подмены ответов API; GitHub Actions запускает эту же команду на каждый push

## Правила

- Коммиты строго по Conventional Commits: `feat:`, `fix:`, `docs:`, `chore:`, `refactor:`, `test:` и т.д. Формат проверяется hk-хуком commit-msg. После клонирования выполнить `hk install --mise`.
- Коммит реализации ссылается на тикет, который он закрывает, например `feat: запись на свободный слот (#12)`. GitHub превращает номер в ссылку, и по истории видно, какой коммит какую задачу делал.
- Не править `.github/workflows/hexlet-check.yml` — это автоматические тесты Хекслета.
- API-контракт для будущих фич: TypeSpec → OpenAPI → клиентский SDK для фронтенда. Вся цепочка перегенерируется одной командой `mise run gen:client`; CI перегенерирует её же и падает при дрейфе (`mise run gen:check` внутри `mise run check`).
- Сгенерированные файлы руками не правим: `spec/tsp-output/openapi.1.yaml` и `frontend/src/shared/api/`. Источник правок — контракт `spec/main.tsp`; изменили контракт → перегенерировали → закоммитили всё вместе.

## Agent skills

### Issue tracker

Задачи и спецификации живут в GitHub Issues этого репозитория (`Iskuskov/ai-for-developers-project-386`), работа через `gh` CLI. См. `docs/agents/issue-tracker.md`.

### Triage labels

Пять канонических ролей triage со строками меток по умолчанию: `needs-triage`, `needs-info`, `ready-for-agent`, `ready-for-human`, `wontfix`. См. `docs/agents/triage-labels.md`.

### Domain docs

Single-context: `GLOSSARY.md` в корне репозитория, решения в `docs/adr/`. См. `docs/agents/domain.md`.
