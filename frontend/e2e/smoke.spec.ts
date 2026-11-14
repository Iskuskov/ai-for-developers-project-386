import { expect, type Page, type Response, test } from '@playwright/test'

/** Сегодняшняя дата строкой в том же виде, что и в приложении (локальная зона). */
function isoDate(dayOffset: number): string {
  const now = new Date()
  const date = new Date(now.getFullYear(), now.getMonth(), now.getDate() + dayOffset)
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${date.getFullYear()}-${month}-${day}`
}

/** Ответ GET /slots на выбранную дату: список слотов { start, available }. */
async function waitForSlots(page: Page, date: string): Promise<Response> {
  return page.waitForResponse(
    (candidate) => candidate.url().includes('/slots') && candidate.url().includes(`date=${date}`),
  )
}

test('гость записывается на свободный слот, владелец видит Запись в списке', async ({ page }) => {
  // Гость: главная → «Записаться →» → типы встреч
  await page.goto('/')
  await expect(page.getByRole('heading', { level: 1, name: 'Календарь звонков' })).toBeVisible()
  await page.getByRole('link', { name: 'Записаться →' }).click()
  await expect(page.getByRole('heading', { name: 'Типы встреч' })).toBeVisible()

  // Выбор seed-типа «Знакомство» на карточке
  const greetingCard = page.locator('.mantine-Card-root').filter({ hasText: 'Знакомство' })
  await expect(greetingCard).toContainText('Длительность: 15 мин')
  await greetingCard.getByRole('link', { name: 'Записаться' }).click()
  await expect(page.getByRole('heading', { name: 'Календарь: Знакомство' })).toBeVisible()

  // Календарь: выбираем «завтра» — внутри 14-дневного окна записи, все его слоты свободны
  // (у «сегодня» слоты могли бы уже пройти в рабочей зоне Europe/Moscow)
  const tomorrow = isoDate(1)
  const dayGroup = page.getByRole('group', { name: 'Дни записи' })
  const pendingSlots = waitForSlots(page, tomorrow)
  await dayGroup.getByRole('button').nth(1).click()
  const slots = (await (await pendingSlots).json()) as { start: string; available: boolean }[]
  const slotStart = slots.find((slot) => slot.available)?.start
  expect(slotStart, 'на выбранный день есть свободный слот').toBeTruthy()

  // Выбор свободного слота по его подписи «ЧЧ:ММ»
  await page.getByRole('button', { name: slotStart!.slice(11, 16) }).click()
  await expect(page.getByText('Выбрано время:')).toBeVisible()

  // Форма записи: имя и email, отправка
  await page.getByLabel('Имя').fill('Гость e2e')
  await page.getByLabel('Email').fill('guest@example.com')
  await page.getByRole('button', { name: 'Записаться' }).click()

  // Инлайн-подтверждение вместо формы
  await expect(page.getByRole('status')).toContainText('Вы записаны: Знакомство')

  // Владелец: список Записей содержит эту запись
  await page.getByRole('link', { name: '← К типам встреч' }).click()
  await expect(page.getByRole('heading', { name: 'Типы встреч' })).toBeVisible()
  await page.getByRole('link', { name: 'Записи' }).click()
  await expect(page.getByRole('heading', { name: 'Записи' })).toBeVisible()
  await expect(page.getByRole('heading', { name: 'Знакомство' }).first()).toBeVisible()
  await expect(page.getByText('Гость: Гость e2e').first()).toBeVisible()
  await expect(page.getByText('guest@example.com').first()).toBeVisible()
})
