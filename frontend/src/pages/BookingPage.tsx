import {
  Anchor,
  Box,
  Container,
  Divider,
  Stack,
  Text,
  Title,
} from '@mantine/core'
import { Link } from 'react-router'

export function BookingPage() {
  return (
    <Box mih="100vh" bg="gray.0">
      <Box bg="white" pb="md" pt="md" style={{ borderBottom: '1px solid var(--mantine-color-gray-2)' }}>
        <Container size="lg">
          <Anchor component={Link} to="/" fw={700} c="dark" underline="never">
            Календарь звонков
          </Anchor>
        </Container>
      </Box>
      <Container size="sm" py="xl">
        <Stack gap="md">
          <Title order={2}>Запись на звонок</Title>
          <Text c="dimmed">
            Здесь появится выбор типа встречи, календарь на ближайшие 14 дней и подтверждение
            записи. Запись откроется на следующем шаге.
          </Text>
          <Divider />
          <Anchor component={Link} to="/" size="sm">
            ← На главную
          </Anchor>
        </Stack>
      </Container>
    </Box>
  )
}
