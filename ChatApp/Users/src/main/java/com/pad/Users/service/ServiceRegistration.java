package com.pad.Users.service;

import com.orbitz.consul.AgentClient;
import com.orbitz.consul.Consul;
import com.orbitz.consul.NotRegisteredException;
import com.orbitz.consul.model.agent.ImmutableRegistration;
import com.orbitz.consul.model.agent.Registration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@Slf4j
public class ServiceRegistration {
  public static void registerService() throws NotRegisteredException {
    Consul client = Consul.builder().withUrl("http://consul:8500/").build();

    AgentClient agentClient = client.agentClient();
    String serviceId = "UsersService-9300";

    log.info("Registering to consul with ID :" + serviceId);

    Registration service =
        ImmutableRegistration.builder()
            .id(serviceId)
            .name("UsersService")
            .port(80)
            .address("usersservice")
            .check(Registration.RegCheck.ttl(3600L))
            .tags(Collections.emptyList())
            .meta(Collections.emptyMap())
            .build();

    agentClient.register(service);
    agentClient.pass(serviceId);
  }
}
