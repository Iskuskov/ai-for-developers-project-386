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
import { Link } from 'react-router'
import { listBookings, listEventTypes } from '../shared/api'
import { AppHeader } from '../shared/AppHeader'
import { useApi } from '../shared/useApi'

/** Дата и время старта Записи для показа владельцу: «21.01.2026, 10:00». */
function formatSlotStart(slotStart: string) {
  const [date, time] = slotStart.split('T')
  const [year, month, day] = date.split('-')
  return `${day}.${month}.${year}, ${time.slice(0, 5)}`
}

export function BookingsPage() {
  const { data: bookings, hasError: bookingsFailed } = useApi(listBookings, [])
  const { data: eventTypes, hasError: eventTypesFailed } = useApi(listEventTypes, [])
  const hasError = bookingsFailed || eventTypesFailed
  const titlesById = new Map((eventTypes ?? []).map((eventType) => [eventType.id, eventType.title]))

  return (
    <Box mih="100vh" bg="gray.0">
      <AppHeader />
      <Container size="sm" py="xl">
        <Stack gap="md">
          <Title order={2}>Записи</Title>
          {hasError && <Text c="red">Не удалось загрузить записи</Text>}
          {!hasError && bookings === null && (
            <Group justify="center" py="xl">
              <Loader size="sm" />
            </Group>
          )}
          {!hasError && bookings !== null && bookings.length === 0 && (
            <Text c="dimmed">Пока нет записей</Text>
          )}
          {!hasError && bookings !== null && bookings.length > 0 && (
            <Stack gap="sm">
              {bookings.map((booking) => (
                <Card key={booking.id} withBorder shadow="sm" radius="md" bg="white">
                  <Stack gap="xs">
                    <Title order={3}>
                      {titlesById.get(booking.eventTypeId) ?? booking.eventTypeId}
                    </Title>
                    <Text>{formatSlotStart(booking.slotStart)}</Text>
                    <Text>Гость: {booking.guestName}</Text>
                    <Text c="dimmed">{booking.guestEmail}</Text>
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
