import {
  Anchor,
  Box,
  Button,
  Container,
  Group,
  Loader,
  Stack,
  Text,
  TextInput,
  Title,
} from '@mantine/core'
import { useEffect, useMemo, useState } from 'react'
import type { FormEvent } from 'react'
import { Link, useParams } from 'react-router'
import { createBooking, listEventTypes, listSlots } from '../shared/api'
import type { EventType, Slot } from '../shared/api'

const DAY_COUNT = 14

/** Сегодняшняя дата в Europe/Moscow (YYYY-MM-DD) независимо от часового пояса браузера. */
function moscowTodayIso(): string {
  return new Intl.DateTimeFormat('en-CA', {
    timeZone: 'Europe/Moscow',
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
  }).format(new Date())
}

/** Дата в UTC-представлении: полоса дней строится по календарю Europe/Moscow без DST-сдвигов. */
function isoDate(date: Date): string {
  const month = String(date.getUTCMonth() + 1).padStart(2, '0')
  const day = String(date.getUTCDate()).padStart(2, '0')
  return `${date.getUTCFullYear()}-${month}-${day}`
}

function dayLabel(date: Date): string {
  return new Intl.DateTimeFormat('ru-RU', {
    timeZone: 'UTC',
    weekday: 'short',
    day: 'numeric',
    month: 'short',
  }).format(date)
}

function slotLabel(start: string): string {
  return start.slice(11, 16)
}

export function SlotCalendarPage() {
  const { eventTypeId } = useParams()
  const [eventType, setEventType] = useState<EventType | null>(null)
  const [slots, setSlots] = useState<Slot[] | null>(null)
  const [hasError, setHasError] = useState(false)
  const [selectedDate, setSelectedDate] = useState(() => moscowTodayIso())
  const [selectedStart, setSelectedStart] = useState<string | null>(null)
  const [guestName, setGuestName] = useState('')
  const [guestEmail, setGuestEmail] = useState('')
  const [confirmation, setConfirmation] = useState<string | null>(null)
  const [formError, setFormError] = useState<string | null>(null)
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [reloadToken, setReloadToken] = useState(0)

  const days = useMemo(() => {
    const [year, month, day] = moscowTodayIso().split('-').map(Number)
    const today = new Date(Date.UTC(year, month - 1, day))
    const result: { date: string; label: string }[] = []
    for (let index = 0; index < DAY_COUNT; index += 1) {
      const day = new Date(today)
      day.setUTCDate(today.getUTCDate() + index)
      result.push({ date: isoDate(day), label: dayLabel(day) })
    }
    return result
  }, [])

  useEffect(() => {
    let cancelled = false
    listEventTypes()
      .then((result) => {
        if (!cancelled) {
          setEventType(result.data?.find((type) => type.id === eventTypeId) ?? null)
        }
      })
      .catch(() => {
        if (!cancelled) {
          setEventType(null)
        }
      })
    return () => {
      cancelled = true
    }
  }, [eventTypeId])

  useEffect(() => {
    if (!eventTypeId) {
      return
    }
    let cancelled = false
    setSelectedStart(null)
    setSlots(null)
    setHasError(false)
    listSlots({ query: { eventTypeId, date: selectedDate } })
      .then((result) => {
        if (!cancelled) {
          setSlots(result.data ?? [])
        }
      })
      .catch(() => {
        if (!cancelled) {
          setHasError(true)
        }
      })
    return () => {
      cancelled = true
    }
  }, [eventTypeId, selectedDate, reloadToken])

  async function handleBookingSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    if (!eventTypeId || !selectedStart) {
      return
    }
    setIsSubmitting(true)
    setFormError(null)
    const result = await createBooking({
      body: { eventTypeId, slotStart: selectedStart, guestName, guestEmail },
    })
    setIsSubmitting(false)
    if (result.error) {
      setFormError(result.error.message)
      if (result.error.code === 'SLOT_TAKEN') {
        setSelectedStart(null)
        setReloadToken((token) => token + 1)
      }
      return
    }
    const booking = result.data
    if (booking && eventType) {
      setConfirmation(`Вы записаны: ${eventType.title}, ${slotLabel(booking.slotStart)}`)
      setGuestName('')
      setGuestEmail('')
    }
  }

  return (
    <Box mih="100vh" bg="gray.0">
      <Box bg="white" pb="md" pt="md" style={{ borderBottom: '1px solid var(--mantine-color-gray-2)' }}>
        <Container size="lg">
          <Anchor component={Link} to="/booking" fw={700} c="dark" underline="never">
            Календарь звонков
          </Anchor>
        </Container>
      </Box>
      <Container size="sm" py="xl">
        <Stack gap="md">
          <Title order={2}>{eventType ? `Календарь: ${eventType.title}` : 'Календарь записи'}</Title>
          {eventType && (
            <Text c="dimmed">
              {eventType.description} · Длительность: {eventType.durationMinutes} мин
            </Text>
          )}
          <Group role="group" aria-label="Дни записи" gap="xs" wrap="wrap">
            {days.map((day) => (
              <Button
                key={day.date}
                size="sm"
                variant={day.date === selectedDate ? 'filled' : 'default'}
                onClick={() => setSelectedDate(day.date)}
              >
                {day.label}
              </Button>
            ))}
          </Group>
          {hasError && <Text c="red">Не удалось загрузить слоты</Text>}
          {!hasError && slots === null && (
            <Group justify="center" py="xl">
              <Loader size="sm" />
            </Group>
          )}
          {!hasError && slots !== null && (
            <Group gap="xs" wrap="wrap">
              {slots.map((slot) => (
                <Button
                  key={slot.start}
                  variant={slot.start === selectedStart ? 'filled' : 'default'}
                  disabled={!slot.available}
                  aria-pressed={slot.available ? slot.start === selectedStart : undefined}
                  onClick={() => setSelectedStart(slot.start)}
                >
                  {slotLabel(slot.start)}
                </Button>
              ))}
            </Group>
          )}
          {selectedStart && <Text>Выбрано время: {slotLabel(selectedStart)}</Text>}
          {confirmation && (
            <Text c="green" role="status">
              {confirmation}
            </Text>
          )}
          {eventType && !confirmation && (
            <Box component="section" aria-label="Форма записи">
              <Title order={3}>Запись</Title>
              <form onSubmit={handleBookingSubmit}>
                <Stack gap="sm">
                  <TextInput
                    label="Имя"
                    placeholder="Как к вам обращаться"
                    value={guestName}
                    onChange={(change) => setGuestName(change.currentTarget.value)}
                  />
                  <TextInput
                    label="Email"
                    placeholder="guest@example.com"
                    value={guestEmail}
                    onChange={(change) => setGuestEmail(change.currentTarget.value)}
                  />
                  {formError && <Text c="red">{formError}</Text>}
                  <Button type="submit" w="fit-content" disabled={!selectedStart || isSubmitting}>
                    Записаться
                  </Button>
                </Stack>
              </form>
            </Box>
          )}
          <Anchor component={Link} to="/booking" size="sm">
            ← К типам встреч
          </Anchor>
        </Stack>
      </Container>
    </Box>
  )
}
