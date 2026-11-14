import { Anchor, Box, Button, Container, Divider, Group, Loader, Stack, Text, Title } from '@mantine/core'
import { Link } from 'react-router'
import { listEventTypes } from '../shared/api'
import { EventTypeCard } from '../shared/EventTypeCard'
import { AppHeader } from '../shared/AppHeader'
import { useApi } from '../shared/useApi'

export function BookingPage() {
  const { data: eventTypes, hasError } = useApi(listEventTypes, [])

  return (
    <Box mih="100vh" bg="gray.0">
      <AppHeader />
      <Container size="sm" py="xl">
        <Stack gap="md">
          <Title order={2}>Типы встреч</Title>
          {hasError && <Text c="red">Не удалось загрузить типы встреч</Text>}
          {!hasError && eventTypes === null && (
            <Group justify="center" py="xl">
              <Loader size="sm" />
            </Group>
          )}
          {!hasError && eventTypes !== null && (
            <Stack gap="sm">
              {eventTypes.map((eventType) => (
                <EventTypeCard key={eventType.id} eventType={eventType}>
                  <Button component={Link} to={`/booking/${eventType.id}`}>
                    Записаться
                  </Button>
                </EventTypeCard>
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
