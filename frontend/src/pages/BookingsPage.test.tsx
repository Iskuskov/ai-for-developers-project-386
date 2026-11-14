import { render, screen } from '../../test-utils'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import { listBookings, listEventTypes } from '../shared/api'
import type { Booking, EventType } from '../shared/api'
import { BookingsPage } from './BookingsPage'

vi.mock('../shared/api', () => ({
  listBookings: vi.fn(),
  listEventTypes: vi.fn(),
}))

const listBookingsMock = vi.mocked(listBookings)
const listEventTypesMock = vi.mocked(listEventTypes)

const eventTypes: EventType[] = [
  {
    id: 'consultation',
    title: 'Консультация',
    description: 'Разбор вопроса или задачи в течение получаса.',
    durationMinutes: 30,
  },
  {
    id: 'greeting',
    title: 'Знакомство',
    description: 'Короткий звонок, чтобы познакомиться и понять, чем можно помочь.',
    durationMinutes: 15,
  },
]

const bookings: Booking[] = [
  {
    id: 'b-1',
    eventTypeId: 'consultation',
    slotStart: '2026-01-21T10:00:00',
    guestName: 'Александр',
    guestEmail: 'guest@example.com',
  },
  {
    id: 'b-2',
    eventTypeId: 'greeting',
    slotStart: '2026-01-22T09:30:00',
    guestName: 'Мария',
    guestEmail: 'maria@example.com',
  },
]

describe('BookingsPage', () => {
  beforeEach(() => {
    listBookingsMock.mockReset()
    listEventTypesMock.mockReset()
    listEventTypesMock.mockResolvedValue({ data: eventTypes } as Awaited<
      ReturnType<typeof listEventTypes>
    >)
    listBookingsMock.mockResolvedValue({ data: bookings } as Awaited<
      ReturnType<typeof listBookings>
    >)
  })

  it('показывает предстоящие Записи всех типов: время, тип, имя и email гостя', async () => {
    render(<BookingsPage />)

    expect(await screen.findByText('21.01.2026, 10:00')).toBeInTheDocument()
    expect(screen.getByText('Консультация')).toBeInTheDocument()
    expect(screen.getByText('Гость: Александр')).toBeInTheDocument()
    expect(screen.getByText('guest@example.com')).toBeInTheDocument()
    expect(screen.getByText('22.01.2026, 09:30')).toBeInTheDocument()
    expect(screen.getByText('Знакомство')).toBeInTheDocument()
    expect(screen.getByText('Гость: Мария')).toBeInTheDocument()
    expect(screen.getByText('maria@example.com')).toBeInTheDocument()
  })

  it('показывает пустое состояние, когда предстоящих Записей нет', async () => {
    listBookingsMock.mockResolvedValue({ data: [] as Booking[] } as Awaited<
      ReturnType<typeof listBookings>
    >)
    render(<BookingsPage />)

    expect(await screen.findByText('Пока нет записей')).toBeInTheDocument()
  })
})
