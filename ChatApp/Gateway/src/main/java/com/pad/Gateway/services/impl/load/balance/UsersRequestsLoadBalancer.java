package com.pad.Gateway.services.impl.load.balance;

import com.pad.Gateway.entity.AvailableUsersService;
import com.pad.Gateway.services.AvailableServicesLookup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class UsersRequestsLoadBalancer {

  @Autowired AvailableServicesLookup availableServicesLookup;

  private static final AtomicInteger ind = new AtomicInteger(0);

  public AvailableUsersService getNextAvailableService() {

    List<AvailableUsersService> availableUsersServices =
        availableServicesLookup.getAvailableUsersServices();

    int serviceIndex = ind.getAndAccumulate(availableUsersServices.size(), (cur, n)->cur >= n-1 ? 0 : cur+1);
    AvailableUsersService usersService = availableUsersServices.get(serviceIndex);

    log.info(
        "Sending request to users service with address: "
            + usersService.getAddress()
            + ":"
            + usersService.getPort());

    return usersService;
  }
}
