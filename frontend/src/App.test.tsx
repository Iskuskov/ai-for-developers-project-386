import '@testing-library/jest-dom/vitest'
import userEvent from '@testing-library/user-event'
import { render, screen } from '../test-utils'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import { listEventTypes } from './shared/api'
import type { EventType } from './shared/api'
import App from './App'

vi.mock('./shared/api', () => ({
  listEventTypes: vi.fn(),
  createEventType: vi.fn(),
}))

const listEventTypesMock = vi.mocked(listEventTypes)

const seed: EventType[] = [
  {
    id: 'greeting',
    title: 'Знакомство',
    description: 'Короткий звонок, чтобы познакомиться и понять, чем можно помочь.',
    durationMinutes: 15,
  },
]

describe('App', () => {
  beforeEach(() => {
    listEventTypesMock.mockReset()
    listEventTypesMock.mockResolvedValue({ data: seed, error: undefined })
  })

  it('renders landing title on the main page', () => {
    render(<App />)
    expect(screen.getByRole('heading', { level: 1, name: 'Календарь звонков' })).toBeInTheDocument()
    expect(
      screen.getByRole('link', { name: 'Записаться →' }),
    ).toBeInTheDocument()
  })

  it('navigates from the landing to the booking page', async () => {
    const user = userEvent.setup()
    render(<App />)
    await user.click(screen.getByRole('link', { name: 'Записаться →' }))
    expect(screen.getByRole('heading', { name: 'Виды брони' })).toBeInTheDocument()
    expect(await screen.findByRole('heading', { name: 'Знакомство' })).toBeInTheDocument()
    expect(screen.getByRole('link', { name: '← На главную' })).toBeInTheDocument()
  })

  it('shows the booking page with event types when opened directly', async () => {
    render(<App />, { route: '/booking' })
    expect(screen.getByRole('heading', { name: 'Виды брони' })).toBeInTheDocument()
    expect(await screen.findByRole('heading', { name: 'Знакомство' })).toBeInTheDocument()
    expect(screen.getByRole('link', { name: '← На главную' })).toBeInTheDocument()
  })

  it('navigates to the event types page from the shared header', async () => {
    const user = userEvent.setup()
    render(<App />, { route: '/booking' })
    await user.click(screen.getByRole('link', { name: 'Типы встреч' }))

    expect(screen.getByRole('heading', { name: 'Типы встреч' })).toBeInTheDocument()
    expect(await screen.findByRole('heading', { name: 'Знакомство' })).toBeInTheDocument()
  })
})
