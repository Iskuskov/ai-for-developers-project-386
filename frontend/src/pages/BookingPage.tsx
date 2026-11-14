import {
  Anchor,
  Box,
  Card,
  Container,
  Divider,
  Group,
  Loader,
  Stack,
  Text,
  Title,
} from '@mantine/core'
import { useEffect, useState } from 'react'
import { Link } from 'react-router'
import { listEventTypes } from '../shared/api'
import type { EventType } from '../shared/api'
import { AppHeader } from '../shared/AppHeader'

export function BookingPage() {
  const [eventTypes, setEventTypes] = useState<EventType[] | null>(null)
  const [hasError, setHasError] = useState(false)

  useEffect(() => {
    let cancelled = false
    listEventTypes()
      .then((result) => {
        if (!cancelled) {
          setEventTypes(result.data ?? [])
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
  }, [])

  return (
    <Box mih="100vh" bg="gray.0">
      <AppHeader />
      <Container size="sm" py="xl">
        <Stack gap="md">
          <Title order={2}>Виды брони</Title>
          {hasError && <Text c="red">Не удалось загрузить типы встреч</Text>}
          {!hasError && eventTypes === null && (
            <Group justify="center" py="xl">
              <Loader size="sm" />
            </Group>
          )}
          {!hasError && eventTypes !== null && (
            <Stack gap="sm">
              {eventTypes.map((eventType) => (
                <Card key={eventType.id} withBorder shadow="sm" radius="md" bg="white">
                  <Stack gap="xs">
                    <Title order={3}>{eventType.title}</Title>
                    <Text c="dimmed">{eventType.description}</Text>
                    <Text size="sm">Длительность: {eventType.durationMinutes} мин</Text>
                  </Stack>
                </Card>
              ))}
            </Stack>
          )}
          <Divider />
          <Anchor component={Link} to="/" size="sm">
            ← На главную
          </Anchor>
        </Stack>
      </Container>
    </Box>
  )
}
