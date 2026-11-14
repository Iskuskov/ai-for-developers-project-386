import userEvent from '@testing-library/user-event'
import { render, screen } from '../../test-utils'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import { createEventType, listEventTypes } from '../shared/api'
import type { EventType } from '../shared/api'
import { EventTypesPage } from './EventTypesPage'

vi.mock('../shared/api', () => ({
  listEventTypes: vi.fn(),
  createEventType: vi.fn(),
}))

const listEventTypesMock = vi.mocked(listEventTypes)
const createEventTypeMock = vi.mocked(createEventType)

const seed: EventType[] = [
  {
    id: 'greeting',
    title: 'Знакомство',
    description: 'Короткий звонок, чтобы познакомиться и понять, чем можно помочь.',
    durationMinutes: 15,
  },
]

describe('EventTypesPage', () => {
  beforeEach(() => {
    listEventTypesMock.mockReset()
    createEventTypeMock.mockReset()
    listEventTypesMock.mockResolvedValue({ data: seed } as Awaited<
      ReturnType<typeof listEventTypes>
    >)
  })

  it('создаёт Тип встречи через сгенерированный SDK и показывает его в списке', async () => {
    const created: EventType = {
      id: 'new-1',
      title: 'Планирование',
      description: 'Совместное планирование задач.',
      durationMinutes: 45,
    }
    createEventTypeMock.mockResolvedValue({ data: created } as Awaited<
      ReturnType<typeof createEventType>
    >)
    const user = userEvent.setup()
    render(<EventTypesPage />)

    expect(await screen.findByRole('heading', { name: 'Знакомство' })).toBeInTheDocument()

    await user.type(screen.getByLabelText('Название'), 'Планирование')
    await user.type(screen.getByLabelText('Описание'), 'Совместное планирование задач.')
    await user.type(screen.getByLabelText('Длительность, минут'), '45')
    await user.click(screen.getByRole('button', { name: 'Создать' }))

    expect(createEventTypeMock).toHaveBeenCalledWith({
      body: { title: 'Планирование', description: 'Совместное планирование задач.', durationMinutes: 45 },
    })
    expect(await screen.findByRole('heading', { name: 'Планирование' })).toBeInTheDocument()
    expect(screen.getByText('Длительность: 45 мин')).toBeInTheDocument()
  })

  it('показывает сообщение ошибки, когда сервер вернул 400', async () => {
    createEventTypeMock.mockResolvedValue({
      error: { code: 'VALIDATION_FAILED', message: 'Название не может быть пустым' },
    } as Awaited<ReturnType<typeof createEventType>>)
    const user = userEvent.setup()
    render(<EventTypesPage />)

    expect(await screen.findByRole('heading', { name: 'Знакомство' })).toBeInTheDocument()

    await user.click(screen.getByRole('button', { name: 'Создать' }))

    expect(await screen.findByText('Название не может быть пустым')).toBeInTheDocument()
  })
})
