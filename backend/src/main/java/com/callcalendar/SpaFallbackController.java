package com.callcalendar;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.boot.webmvc.error.ErrorController;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Отдаёт index.html для клиентских маршрутов SPA (навигация браузера с Accept: text/html),
 * остальное (запросы API без HTML-accept) отвечает единым телом ошибки. Тело читается из classpath
 * напрямую: без forward, без циклов error-диспетча, при несобранной статике остаётся 404.
 */
@Controller
public class SpaFallbackController implements ErrorController {

  private static final ClassPathResource INDEX = new ClassPathResource("static/index.html");

  @RequestMapping("/error")
  public void error(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Integer status = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
    boolean notFound = status != null && status == 404;
    String accept = request.getHeader(HttpHeaders.ACCEPT);
    boolean htmlNavigation = notFound && accept != null && accept.contains("text/html");
    if (htmlNavigation && INDEX.exists()) {
      response.setStatus(HttpServletResponse.SC_OK);
      response.setContentType("text/html;charset=UTF-8");
      try (InputStream in = INDEX.getInputStream()) {
        in.transferTo(response.getOutputStream());
      }
      return;
    }
    response.setStatus(status != null ? status : HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    response.setContentType("application/json");
    response.getWriter().write("{\"code\":\"NOT_FOUND\",\"message\":\"Resource not found\"}");
  }
}
