package com.pad.Gateway.services.impl.load.balance;

import com.pad.Gateway.entity.AvailableUsersService;
import com.pad.Gateway.services.AvailableServicesLookup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class UsersRequestsLoadBalancer {

  @Autowired AvailableServicesLookup availableServicesLookup;

  public AvailableUsersService getNextAvailableService() {

    List<AvailableUsersService> availableUsersServices =
        availableServicesLookup.getAvailableUsersServices();

    return availableUsersServices.get(new Random().nextInt(availableUsersServices.size()));
  }
}
