package com.pad.Gateway.services;

import com.orbitz.consul.HealthClient;
import com.orbitz.consul.model.ConsulResponse;
import com.orbitz.consul.model.health.ServiceHealth;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;

import java.util.LinkedList;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class AvailableServicesLookupTest {

  @Mock AvailableServicesLookup availableServicesLookup;

  @Mock HealthClient healthClient;

  @BeforeEach
  void setUp() {
    availableServicesLookup = mock(AvailableServicesLookup.class);
    healthClient = mock(HealthClient.class);
    doCallRealMethod().when(availableServicesLookup).lookupForServices();
  }

  @Test
  public void testNoAvailableServices() {
    ConsulResponse response = mock(ConsulResponse.class);
    when(response.getResponse()).thenReturn(new LinkedList<ServiceHealth>());

    when(healthClient.getHealthyServiceInstances("ChatSessionService")).thenReturn(response);
    when(healthClient.getHealthyServiceInstances("UserSessionService")).thenReturn(response);

    availableServicesLookup.lookupForServices();

    assertEquals(0, availableServicesLookup.getAvailableChatServices().size());
    assertEquals(0, availableServicesLookup.getAvailableUsersServices().size());
  }

  @Test
  public void testvailableServicesFound() {
    ServiceHealth serviceHealth = mock(ServiceHealth.class);

    ConsulResponse response = mock(ConsulResponse.class);
    when(response.getResponse()).thenReturn(new LinkedList<ServiceHealth>().add(serviceHealth));

    when(healthClient.getHealthyServiceInstances("ChatSessionService")).thenReturn(response);
    when(healthClient.getHealthyServiceInstances("UserSessionService")).thenReturn(response);

    availableServicesLookup.lookupForServices();

    assertEquals(1, availableServicesLookup.getAvailableChatServices().size());
    assertEquals(1, availableServicesLookup.getAvailableUsersServices().size());
  }
}
