package com.pad.Gateway.services;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class GrpcServerImpl {
  @EventListener(ApplicationReadyEvent.class)
  public void initGrpcServer() throws IOException, InterruptedException {
    Server grpcServer = ServerBuilder.forPort(8081).addService(new MessagesServiceImpl()).build();
    grpcServer.start();

    log.info("gRPC server started!");

    grpcServer.awaitTermination();
  }
}
