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
