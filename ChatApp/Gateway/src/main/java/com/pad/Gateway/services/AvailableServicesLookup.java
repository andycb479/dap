package com.pad.Gateway.services;

import com.pad.Gateway.entity.AvailableService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedList;
import java.util.List;

@Service
@Slf4j
public class AvailableServicesLookup {
  @Value("${consul.url}")
  public static String consul_url;

  private final RestTemplate restTemplate = new RestTemplateBuilder().build();

  private List<AvailableService> availableServices = new LinkedList<>();

  @EventListener(ApplicationReadyEvent.class)
  public void lookupForServices() {
    // once in x minutes will check for available services
    log.info("Looking up for services...");

    availableServices.add(new AvailableService("9100"));
  }

  public List<AvailableService> getAvailableServices() {
    return availableServices;
  }
}
