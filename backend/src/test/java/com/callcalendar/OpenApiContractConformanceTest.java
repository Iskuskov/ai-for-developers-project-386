package com.callcalendar;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.yaml.snakeyaml.Yaml;

/**
 * Тест соответствия контракту: OpenAPI, отдаваемый бекендом (springdoc), совпадает со спекой,
 * сгенерированной из TypeSpec. Расхождение валит сборку.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
class OpenApiContractConformanceTest {

  /** Метаданные документа, не влияющие на контракт: точная версия формата и адреса серверов. */
  private static final List<String> NON_CONTRACT_KEYS = List.of("openapi", "servers");

  @Autowired private RestTestClient client;

  @Test
  void springdocOpenApiMatchesTypeSpecContract() {
    Map<String, Object> served = fetchServedOpenApi();
    Map<String, Object> fromTypeSpec = loadTypeSpecOpenApi();

    assertThat(served.get("openapi")).asString().startsWith("3.1");
    assertThat(fromTypeSpec.get("openapi")).asString().isEqualTo("3.1.0");

    assertThat(normalize(served))
        .as("OpenAPI из springdoc (/v3/api-docs) должен совпадать со спекой из TypeSpec")
        .isEqualTo(normalize(fromTypeSpec));
  }

  private Map<String, Object> fetchServedOpenApi() {
    Map<String, Object> body =
        client
            .get()
            .uri("/v3/api-docs")
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(new ParameterizedTypeReference<Map<String, Object>>() {})
            .returnResult()
            .getResponseBody();
    assertThat(body).isNotNull();
    return body;
  }

  private Map<String, Object> loadTypeSpecOpenApi() {
    Path specPath = Path.of("..", "spec", "tsp-output", "openapi.1.yaml");
    try (InputStream in = Files.newInputStream(specPath)) {
      return new Yaml().load(in);
    } catch (IOException e) {
      throw new IllegalStateException(
          "Не удалось прочитать сгенерированную спеку "
              + specPath.toAbsolutePath()
              + " — запустите `mise run gen:spec`",
          e);
    }
  }

  /**
   * Приводит документ к сравнимому виду: отсортированные ключи (порядок полей не важен),
   * отсортированный список required (семантически это множество) и удалённые метаданные, не
   * влияющие на контракт.
   */
  private static Object normalize(Object value) {
    if (value instanceof Map<?, ?> map) {
      TreeMap<Object, Object> sorted = new TreeMap<>();
      map.forEach((key, item) -> sorted.put(key, normalize(item)));
      if (sorted.get("required") instanceof List<?> required) {
        sorted.put("required", required.stream().sorted().toList());
      }
      if (sorted.get("parameters") instanceof List<?> parameters && parameters.isEmpty()) {
        // Отсутствие параметров и пустой список параметров — одно и то же
        sorted.remove("parameters");
      }
      NON_CONTRACT_KEYS.forEach(sorted::remove);
      return sorted;
    }
    if (value instanceof List<?> list) {
      return list.stream().map(OpenApiContractConformanceTest::normalize).toList();
    }
    return value;
  }
}
