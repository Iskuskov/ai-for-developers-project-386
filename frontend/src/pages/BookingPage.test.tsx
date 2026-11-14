import { render, screen } from '../../test-utils'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import { listEventTypes } from '../shared/api'
import type { EventType } from '../shared/api'
import { BookingPage } from './BookingPage'

vi.mock('../shared/api', () => ({
  listEventTypes: vi.fn(),
}))

const listEventTypesMock = vi.mocked(listEventTypes)

const seed: EventType[] = [
  {
    id: 'greeting',
    title: 'Знакомство',
    description: 'Короткий звонок, чтобы познакомиться и понять, чем можно помочь.',
    durationMinutes: 15,
  },
  {
    id: 'consultation',
    title: 'Консультация',
    description: 'Разбор вопроса или задачи в течение получаса.',
    durationMinutes: 30,
  },
]

describe('BookingPage', () => {
  beforeEach(() => {
    listEventTypesMock.mockReset()
  })

  it('renders event types fetched through the generated SDK', async () => {
    listEventTypesMock.mockResolvedValue({ data: seed } as Awaited<ReturnType<typeof listEventTypes>>)
    render(<BookingPage />)

    expect(await screen.findByRole('heading', { name: 'Знакомство' })).toBeInTheDocument()
    expect(
      screen.getByText('Короткий звонок, чтобы познакомиться и понять, чем можно помочь.'),
    ).toBeInTheDocument()
    expect(screen.getByRole('heading', { name: 'Консультация' })).toBeInTheDocument()
    expect(screen.getByText('Длительность: 15 мин')).toBeInTheDocument()
    expect(screen.getByText('Длительность: 30 мин')).toBeInTheDocument()
  })

  it('shows an error message when the request fails', async () => {
    listEventTypesMock.mockRejectedValue(new Error('network down'))
    render(<BookingPage />)

    expect(await screen.findByText('Не удалось загрузить типы встреч')).toBeInTheDocument()
  })

  it('links each event type to its slot calendar page', async () => {
    listEventTypesMock.mockResolvedValue({ data: seed } as Awaited<ReturnType<typeof listEventTypes>>)
    render(<BookingPage />)

    const links = await screen.findAllByRole('link', { name: 'Записаться' })
    expect(links).toHaveLength(2)
    expect(links[0]).toHaveAttribute('href', '/booking/greeting')
    expect(links[1]).toHaveAttribute('href', '/booking/consultation')
  })
})
