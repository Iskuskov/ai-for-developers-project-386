package com.callcalendar;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.stereotype.Component;

/** In-memory хранилище Типов встреч: seed-список плюс созданные владельцем за жизнь процесса. */
@Component
public class EventTypeStore {

  private final List<EventType> items = new CopyOnWriteArrayList<>(EventType.seed());

  /** Все Типы встреч: seed и созданные владельцем. */
  public List<EventType> findAll() {
    return List.copyOf(items);
  }

  /** Тип встречи по идентификатору или пустой Optional, если такого нет. */
  public Optional<EventType> findById(String id) {
    return items.stream().filter(type -> type.id().equals(id)).findFirst();
  }

  /** Создаёт Тип встречи с новым идентификатором и сохраняет его в списке. */
  public EventType create(EventTypeInput input) {
    EventType created =
        new EventType(
            UUID.randomUUID().toString(),
            input.title(),
            input.description(),
            input.durationMinutes());
    items.add(created);
    return created;
  }
}
