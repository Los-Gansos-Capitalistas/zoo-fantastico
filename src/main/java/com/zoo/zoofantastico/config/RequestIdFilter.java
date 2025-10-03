package com.zoo.zoofantastico.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Component
public class RequestIdFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain filterChain)
      throws ServletException, IOException {

    String traceId = Optional.ofNullable(request.getHeader("X-Request-Id"))
        .orElse(UUID.randomUUID().toString());

    // disponible para el handler y los logs
    request.setAttribute("traceId", traceId);
    MDC.put("traceId", traceId);

    // útil para el cliente (Postman) y trazabilidad cruzada
    response.setHeader("X-Request-Id", traceId);

    try {
      filterChain.doFilter(request, response);
    } finally {
      MDC.remove("traceId");
    }
  }
}
