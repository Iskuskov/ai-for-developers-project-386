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
}

@doc("Слот: старт на 30-минутной сетке рабочих часов и признак доступности для записи.")
model Slot {
  @doc("Дата и время старта слота, ISO 8601 (YYYY-MM-DDTHH:mm:ss), время Europe/Moscow.")
  start: string;

  @doc("Доступен ли слот для записи.")
  available: boolean;
}

@doc("Единый формат ошибок API.")
model ApiError {
  @doc("Машинный код ошибки.")
  code: string;

  @doc("Понятное человеку сообщение.")
  message: string;
}

@route("/slots")
@tag("Slots")
interface Slots {
  @operationId("listSlots")
  @doc("Сетка 30-минутных стартов с признаком занятости на запрошенную дату.")
  @get list(
    @doc("Идентификатор типа встречи.")
    @query eventTypeId: string,

    @doc("Дата, на которую запрашиваются слоты (YYYY-MM-DD).")
    @query date: plainDate,
  ): {
    @statusCode status: 200;
    @body slots: Slot[];
  } | {
    @statusCode status: 400;
    @body error: ApiError;
  };
}
