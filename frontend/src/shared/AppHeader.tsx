import { Anchor, Box, Container, Group } from '@mantine/core'
import { Link } from 'react-router'

const links = [
  { to: '/booking', label: 'Запись' },
  { to: '/event-types', label: 'Типы встреч' },
  { to: '/bookings', label: 'Записи' },
]

/** Общая шапка приложения: главная /, Запись, Типы встреч, Записи. */
export function AppHeader() {
  return (
    <Box bg="white" pb="md" pt="md" style={{ borderBottom: '1px solid var(--mantine-color-gray-2)' }}>
      <Container size="lg">
        <Group justify="space-between" wrap="nowrap">
          <Anchor component={Link} to="/" fw={700} c="dark" underline="never">
            Календарь звонков
          </Anchor>
          <Group gap="md" wrap="nowrap">
            {links.map((link) => (
              <Anchor key={link.to} component={Link} to={link.to} underline="never">
                {link.label}
              </Anchor>
            ))}
          </Group>
        </Group>
      </Container>
    </Box>
  )
}
