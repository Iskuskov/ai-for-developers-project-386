import {
  Anchor,
  Box,
  Button,
  Container,
  Group,
  Loader,
  Stack,
  Text,
  Title,
} from '@mantine/core'
import { useEffect, useMemo, useState } from 'react'
import { Link, useParams } from 'react-router'
import { listEventTypes, listSlots } from '../shared/api'
import type { EventType, Slot } from '../shared/api'

const DAY_COUNT = 14

function isoDate(date: Date): string {
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${date.getFullYear()}-${month}-${day}`
}

function dayLabel(date: Date): string {
  return new Intl.DateTimeFormat('ru-RU', {
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
  const [selectedDate, setSelectedDate] = useState(() => isoDate(new Date()))
  const [selectedStart, setSelectedStart] = useState<string | null>(null)

  const days = useMemo(() => {
    const result: { date: string; label: string }[] = []
    const today = new Date()
    for (let index = 0; index < DAY_COUNT; index += 1) {
      const day = new Date(today.getFullYear(), today.getMonth(), today.getDate() + index)
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
  }, [eventTypeId, selectedDate])

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
          <Anchor component={Link} to="/booking" size="sm">
            ← К видам брони
          </Anchor>
        </Stack>
      </Container>
    </Box>
  )
}
