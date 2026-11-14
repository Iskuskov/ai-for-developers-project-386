import { Card, Stack, Text, Title } from '@mantine/core'
import type { ReactNode } from 'react'
import type { EventType } from './api'

/** Карточка Типа встречи: название, описание и длительность; внутри — необязательное действие. */
export function EventTypeCard({
  eventType,
  children,
}: {
  eventType: EventType
  children?: ReactNode
}) {
  return (
    <Card withBorder shadow="sm" radius="md" bg="white">
      <Stack gap="xs">
        <Title order={3}>{eventType.title}</Title>
        <Text c="dimmed">{eventType.description}</Text>
        <Text size="sm">Длительность: {eventType.durationMinutes} мин</Text>
        {children}
      </Stack>
    </Card>
  )
}
