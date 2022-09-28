package com.pad.Gateway.services;

import com.orbitz.consul.Consul;
import com.orbitz.consul.HealthClient;
import com.orbitz.consul.model.health.ServiceHealth;
import com.pad.Gateway.entity.AvailableService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class AvailableServicesLookup {
  private static int refresh_rate;

  private final List<AvailableService> availableServices = new LinkedList<>();

  private List<ServiceHealth> lastScannedNodes = new LinkedList<>();

  private Consul client;

  private HealthClient healthClient;

  @Autowired
  public AvailableServicesLookup(Environment env) {
    refresh_rate = Integer.parseInt(Objects.requireNonNull(env.getProperty("refresh.rate")));
    client = Consul.builder().build();
    healthClient = client.healthClient();
  }

  @EventListener(ApplicationReadyEvent.class)
  public void lookupForServices() {
    // once in x minutes will check for available services
    log.info("Looking up for services...");

    // only in docker env
    // =======================

    Runnable runnable =
        () -> {
          while (true) {
            List<ServiceHealth> nodes =
                healthClient.getHealthyServiceInstances("ChatSessionService").getResponse();

            if (!new HashSet<>(lastScannedNodes).containsAll(nodes)) {
              lastScannedNodes = nodes;
              availableServices.clear();
              lastScannedNodes.forEach(
                  node -> {
                    availableServices.add(
                        new AvailableService(
                            String.valueOf(node.getService().getPort()),
                            node.getService().getAddress()));
                  });
            }
            try {
              Thread.sleep(refresh_rate);
            } catch (InterruptedException e) {
              throw new RuntimeException(e);
            }
          }
        };
    // =======================

//    Thread thread = new Thread(runnable);

    //        thread.start();

    // for dev env
    availableServices.add(new AvailableService("9100", "127.0.0.1"));
  }

  public List<AvailableService> getAvailableServices() {
    return availableServices;
  }
}
