package com.pad.Gateway.services.impl.load.balance.distribution;

import com.pad.Gateway.services.AvailableServicesLookup;

import java.util.function.Supplier;

public interface FinalEntityRequestBuilderAndExecutor {
  Object createAndExecuteRequest(
      Supplier<Object> userSupplier, AvailableServicesLookup availableServicesLookup);
}
