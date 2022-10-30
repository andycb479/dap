package com.pad.Gateway.services.util;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CircuitBreakerContext {
  private static CircuitBreaker userServiceCB;
  private static CircuitBreaker chatServiceCB;

  public static CircuitBreaker getUserServiceCB() {
    if (userServiceCB == null) {
      CircuitBreakerConfig config =
          CircuitBreakerConfig.custom()
              .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
              .slidingWindowSize(10)
              .failureRateThreshold(2.0f)
              .build();
      CircuitBreakerRegistry registry = CircuitBreakerRegistry.of(config);
      CircuitBreaker circuitBreaker = registry.circuitBreaker("userService");
      circuitBreaker.getEventPublisher().onError(e -> log.error("UserService not available!"));
      userServiceCB = circuitBreaker;
    }
    return userServiceCB;
  }

  public static CircuitBreaker getChatServiceCB() {
    if (chatServiceCB == null) {
      CircuitBreakerConfig config =
          CircuitBreakerConfig.custom()
              .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
              .slidingWindowSize(10)
              .failureRateThreshold(2.0f)
              .build();
      CircuitBreakerRegistry registry = CircuitBreakerRegistry.of(config);
      CircuitBreaker circuitBreaker = registry.circuitBreaker("chatService");
      circuitBreaker.getEventPublisher().onError(e -> log.error("ChatService not available!"));
      chatServiceCB = circuitBreaker;
    }
    return chatServiceCB;
  }
}
