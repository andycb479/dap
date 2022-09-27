package com.pad.Gateway.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class AvailableServicesLookup {
  public static final String consul_url = "http://127.0.0.1:8500/v1/agent/services";
  private final RestTemplate restTemplate = new RestTemplateBuilder().build();

  @EventListener(ApplicationReadyEvent.class)
  public void lookupForServices() {

    log.info("Looking up for services...");

  }
}
