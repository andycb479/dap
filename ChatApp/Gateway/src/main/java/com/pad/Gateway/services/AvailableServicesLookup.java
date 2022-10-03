package com.pad.Gateway.services;

import com.orbitz.consul.Consul;
import com.orbitz.consul.HealthClient;
import com.orbitz.consul.model.health.ServiceHealth;
import com.pad.Gateway.entity.AvailableChatService;
import com.pad.Gateway.entity.AvailableUsersService;
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

  private static boolean is_dev_env;

  private final List<AvailableChatService> availableChatServices = new LinkedList<>();
  private final List<AvailableUsersService> availableUsersServices = new LinkedList<>();

  private List<ServiceHealth> lastScannedChatNodes = new LinkedList<>();
  private List<ServiceHealth> lastScannedUsersNodes = new LinkedList<>();

  private Consul client;

  private HealthClient healthClient;

  @Autowired
  public AvailableServicesLookup(Environment env) {
    refresh_rate = Integer.parseInt(Objects.requireNonNull(env.getProperty("refresh.rate")));
    is_dev_env = Boolean.parseBoolean(Objects.requireNonNull(env.getProperty("dev.env")));
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
            List<ServiceHealth> chatNodes =
                healthClient.getHealthyServiceInstances("ChatSessionService").getResponse();
            List<ServiceHealth> usersNodes =
                healthClient.getHealthyServiceInstances("UsersService").getResponse();

            if (!new HashSet<>(lastScannedChatNodes).containsAll(chatNodes)) {
              log.info("Scanning for new ChatSessionService instances...");

              lastScannedChatNodes = chatNodes;
              availableChatServices.clear();
              lastScannedChatNodes.forEach(
                  node -> {
                    availableChatServices.add(
                        new AvailableChatService(
                            String.valueOf(node.getService().getPort()),
                            node.getService().getAddress()));
                  });
            }

            if (!new HashSet<>(lastScannedUsersNodes).containsAll(chatNodes)) {
              log.info("Scanning for new UsersService instances...");

              lastScannedUsersNodes = usersNodes;
              availableUsersServices.clear();
              lastScannedUsersNodes.forEach(
                  node -> {
                    availableUsersServices.add(
                        new AvailableUsersService(
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

    if (is_dev_env) {
      availableChatServices.add(new AvailableChatService("9100", "127.0.0.1"));
      availableUsersServices.add(new AvailableUsersService("9300", "127.0.0.1"));
    } else {
      Thread thread = new Thread(runnable);
      thread.start();
    }
  }

  public List<AvailableChatService> getAvailableChatServices() {
    return availableChatServices;
  }

  public List<AvailableUsersService> getAvailableUsersServices() {
    return availableUsersServices;
  }
}
