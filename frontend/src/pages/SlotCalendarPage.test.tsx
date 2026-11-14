import { render, screen, userEvent, within } from '../../test-utils'
import { Route, Routes } from 'react-router'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import { listEventTypes, listSlots } from '../shared/api'
import type { EventType, Slot } from '../shared/api'
import { SlotCalendarPage } from './SlotCalendarPage'

vi.mock('../shared/api', () => ({
  listEventTypes: vi.fn(),
  listSlots: vi.fn(),
}))

const listEventTypesMock = vi.mocked(listEventTypes)
const listSlotsMock = vi.mocked(listSlots)

const eventType: EventType = {
  id: 'consultation',
  title: 'Консультация',
  description: 'Разбор вопроса или задачи в течение получаса.',
  durationMinutes: 30,
}

const slots: Slot[] = [
  { start: '2026-03-02T09:00:00', available: true },
  { start: '2026-03-02T09:30:00', available: true },
  { start: '2026-03-02T10:00:00', available: false },
  { start: '2026-03-02T10:30:00', available: true },
]

function renderCalendarPage() {
  return render(
    <Routes>
      <Route path="/booking/:eventTypeId" element={<SlotCalendarPage />} />
    </Routes>,
    { route: '/booking/consultation' },
  )
}

describe('SlotCalendarPage', () => {
  beforeEach(() => {
    listEventTypesMock.mockReset()
    listSlotsMock.mockReset()
  })

  it('renders the 14-day window and the slot grid fetched through the generated SDK', async () => {
    listEventTypesMock.mockResolvedValue({
      data: [eventType],
    } as Awaited<ReturnType<typeof listEventTypes>>)
    listSlotsMock.mockResolvedValue({ data: slots } as Awaited<ReturnType<typeof listSlots>>)
    renderCalendarPage()

    expect(await screen.findByRole('heading', { name: /Консультация/ })).toBeInTheDocument()

    const days = screen.getByRole('group', { name: 'Дни записи' })
    expect(within(days).getAllByRole('button')).toHaveLength(14)
    expect(listSlotsMock).toHaveBeenCalledWith({
      query: { eventTypeId: 'consultation', date: expect.any(String) },
    })

    expect(screen.getByRole('button', { name: '09:00' })).toBeEnabled()
    expect(screen.getByRole('button', { name: '10:00' })).toBeDisabled()
  })

  it('clicking an available slot selects it', async () => {
    listEventTypesMock.mockResolvedValue({
      data: [eventType],
    } as Awaited<ReturnType<typeof listEventTypes>>)
    listSlotsMock.mockResolvedValue({ data: slots } as Awaited<ReturnType<typeof listSlots>>)
    renderCalendarPage()

    await userEvent.click(await screen.findByRole('button', { name: '09:00' }))

    expect(screen.getByRole('button', { name: '09:00' })).toHaveAttribute('aria-pressed', 'true')
    expect(screen.getByText('Выбрано время: 09:00')).toBeInTheDocument()
    expect(screen.getByRole('button', { name: '10:00' })).toBeDisabled()
  })
})
