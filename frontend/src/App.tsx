import { AppShell, Container, Text, Title } from '@mantine/core'

function App() {
  return (
    <AppShell header={{ height: 60 }} padding="md">
      <AppShell.Header p="md">
        <Title order={3}>Календарь звонков</Title>
      </AppShell.Header>
      <AppShell.Main>
        <Container>
          <Text>Сервис записи на звонки. Здесь появится календарь бронирования.</Text>
        </Container>
      </AppShell.Main>
    </AppShell>
  )
}

export default App
