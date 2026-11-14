import "@typespec/http";
import "@typespec/openapi";

using TypeSpec.Http;
using TypeSpec.OpenAPI;

@service(#{ title: "Call Calendar API" })
@info(#{ version: "1.0.0" })
namespace CallCalendar;

@doc("Вид звонка: название, описание и длительность.")
model EventType {
  @doc("Идентификатор типа встречи.")
  id: string;

  @doc("Название типа встречи.")
  title: string;

  @doc("Описание для гостей.")
  description: string;

  @doc("Длительность звонка в минутах.")
  durationMinutes: int32;
}

@route("/event-types")
@tag("EventTypes")
interface EventTypes {
  @operationId("listEventTypes")
  @doc("Список типов встреч, доступных для записи.")
  @get list(): EventType[];

  @operationId("createEventType")
  @doc("Создание типа встречи владельцем.")
  @post create(@body requestBody: EventTypeInput): CreatedEventType | ValidationError;
}

@doc("Данные для создания типа встречи: название, описание и длительность.")
model EventTypeInput {
  @doc("Название типа встречи.")
  @minLength(1)
  title: string;

  @doc("Описание для гостей.")
  description: string;

  @doc("Длительность звонка в минутах.")
  @minValueExclusive(0)
  durationMinutes: int32;
}

@doc("Созданный тип встречи.")
model CreatedEventType {
  @statusCode statusCode: 201;

  @body body: EventType;
}

@doc("Ошибка запроса: машиночитаемый код и сообщение для человека.")
model ApiError {
  @doc("Машиночитаемый код ошибки.")
  code: string;

  @doc("Сообщение об ошибке для человека.")
  message: string;
}

@doc("Некорректные данные запроса.")
@error
model ValidationError {
  @statusCode statusCode: 400;

  @body body: ApiError;
}
