package com.pad.Users.service;

import com.orbitz.consul.AgentClient;
import com.orbitz.consul.Consul;
import com.orbitz.consul.NotRegisteredException;
import com.orbitz.consul.model.agent.ImmutableRegistration;
import com.orbitz.consul.model.agent.Registration;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class ServiceRegistration {
    @EventListener(ApplicationReadyEvent.class)
    public void registerService() throws NotRegisteredException {
        Consul client = Consul.builder().build();

        AgentClient agentClient = client.agentClient();

        String serviceId = "UsersService-9300";
        Registration service = ImmutableRegistration.builder()
                .id(serviceId)
                .name("UsersService")
                .port(9300)
                .check(Registration.RegCheck.ttl(3600L))
                .tags(Collections.emptyList())
                .meta(Collections.emptyMap())
                .build();

        agentClient.register(service);
        agentClient.pass(serviceId);
    }
}
