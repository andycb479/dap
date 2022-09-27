package com.pad.Gateway.services;

import com.pad.Gateway.entity.AvailableService;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class AvailableServicesLookup {

  private static String consul_url;

  private static int refresh_rate;

  private final RestTemplate restTemplate = new RestTemplateBuilder().build();

  private final List<AvailableService> availableServices = new LinkedList<>();

  @Autowired
  public AvailableServicesLookup(Environment env) {
    consul_url = env.getProperty("consul.url");
    refresh_rate = Integer.parseInt(Objects.requireNonNull(env.getProperty("refresh.rate")));
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
            String res = restTemplate.getForObject(consul_url, String.class);

            JSONObject jsonObject = new JSONObject(res);

            jsonObject
                .keys()
                .forEachRemaining(
                    key -> {
                      if (key.contains("ChatSessionService")) {

                        JSONObject obj = jsonObject.getJSONObject(key);
                        int port = obj.getInt("Port");
                        String address = obj.getString("Address");

                        // TODO: check if available address is already in list
                        availableServices.add(new AvailableService(String.valueOf(port), address));
                      }
                    });
            try {
              Thread.sleep(refresh_rate);
            } catch (InterruptedException e) {
              throw new RuntimeException(e);
            }
          }
        };
    // =======================

    Thread thread = new Thread(runnable);

    thread.start();

    // for dev env
    availableServices.add(new AvailableService("9100", "127.0.0.1"));
  }

  public List<AvailableService> getAvailableServices() {
    return availableServices;
  }
}
