import {
  Anchor,
  Box,
  Button,
  Container,
  Divider,
  Group,
  Loader,
  NumberInput,
  Stack,
  Text,
  Textarea,
  TextInput,
  Title,
} from '@mantine/core'
import { useState } from 'react'
import type { FormEvent } from 'react'
import { Link } from 'react-router'
import { createEventType, listEventTypes } from '../shared/api'
import type { EventType } from '../shared/api'
import { EventTypeCard } from '../shared/EventTypeCard'
import { AppHeader } from '../shared/AppHeader'
import { useApi } from '../shared/useApi'

export function EventTypesPage() {
  const { data: fetchedTypes, hasError } = useApi(listEventTypes, [])
  // Созданные владельцем Типы встреч добавляются к загруженному списку без повторного запроса
  const [createdTypes, setCreatedTypes] = useState<EventType[]>([])
  const eventTypes = [...(fetchedTypes ?? []), ...createdTypes]
  const [title, setTitle] = useState('')
  const [description, setDescription] = useState('')
  const [durationMinutes, setDurationMinutes] = useState('')
  const [formError, setFormError] = useState<string | null>(null)

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setFormError(null)
    const result = await createEventType({
      body: { title, description, durationMinutes: Number(durationMinutes) },
    })
    if (result.error) {
      setFormError(result.error.message)
      return
    }
    const created = result.data
    if (created) {
      setCreatedTypes((current) => [...current, created])
      setTitle('')
      setDescription('')
      setDurationMinutes('')
    }
  }

  return (
    <Box mih="100vh" bg="gray.0">
      <AppHeader />
      <Container size="sm" py="xl">
        <Stack gap="md">
          <Title order={2}>Типы встреч</Title>
          {hasError && <Text c="red">Не удалось загрузить типы встреч</Text>}
          {!hasError && fetchedTypes === null && (
            <Group justify="center" py="xl">
              <Loader size="sm" />
            </Group>
          )}
          {!hasError && fetchedTypes !== null && (
            <Stack gap="sm">
              {eventTypes.map((eventType) => (
                <EventTypeCard key={eventType.id} eventType={eventType} />
              ))}
            </Stack>
          )}
          <Divider />
          <Title order={3}>Новый тип встречи</Title>
          <form onSubmit={handleSubmit}>
            <Stack gap="sm">
              <TextInput
                label="Название"
                placeholder="Знакомство"
                value={title}
                onChange={(change) => setTitle(change.currentTarget.value)}
              />
              <Textarea
                label="Описание"
                placeholder="Короткий звонок, чтобы познакомиться."
                value={description}
                onChange={(change) => setDescription(change.currentTarget.value)}
              />
              <NumberInput
                label="Длительность, минут"
                placeholder="30"
                value={durationMinutes}
                onChange={(change) => setDurationMinutes(String(change ?? ''))}
              />
              {formError && <Text c="red">{formError}</Text>}
              <Button type="submit" w="fit-content">
                Создать
              </Button>
            </Stack>
          </form>
          <Divider />
          <Anchor component={Link} to="/" size="sm">
            ← На главную
          </Anchor>
        </Stack>
      </Container>
    </Box>
  )
}
