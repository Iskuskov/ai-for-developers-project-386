import {
  Badge,
  Box,
  Button,
  Card,
  Container,
  List,
  SimpleGrid,
  Stack,
  Text,
  Title,
} from '@mantine/core'
import { Link } from 'react-router'

export function LandingPage() {
  return (
    <Box
      component="section"
      style={(theme) => ({
        minHeight: '100vh',
        display: 'flex',
        alignItems: 'center',
        background: `linear-gradient(120deg, ${theme.colors.blue[1]} 0%, ${theme.colors.gray[1]} 45%, ${theme.colors.orange[1]} 100%)`,
      })}
    >
      <Container size="lg" py="xl" w="100%">
        <SimpleGrid cols={{ base: 1, md: 2 }} spacing="xl" verticalSpacing="xl">
          <Stack gap="md" justify="center">
            <Badge variant="light" color="orange" size="lg" w="fit-content">
              Быстрая запись на звонок
            </Badge>
            <Title order={1} fw={800}>
              Календарь звонков
            </Title>
            <Text size="lg" c="dimmed" maw={480}>
              Один экран, понятные слоты, быстрая бронь. Выберите время и запишитесь на звонок без
              лишних шагов.
            </Text>
            <Button component={Link} to="/booking" size="lg" w="fit-content" mt="xs">
              Записаться →
            </Button>
          </Stack>
          <Card withBorder radius="lg" shadow="sm" padding="xl" bg="white">
            <Title order={3} mb="md">
              Что доступно прямо сейчас
            </Title>
            <List spacing="sm" size="sm" c="dimmed">
              <List.Item>Слоты с шагом 30 минут, будни 10:00–19:00 (Europe/Moscow)</List.Item>
              <List.Item>
                Одно и то же время нельзя занять дважды — даже разными типами встреч
              </List.Item>
              <List.Item>Предстоящие события — в отдельном разделе</List.Item>
            </List>
          </Card>
        </SimpleGrid>
      </Container>
    </Box>
  )
}
