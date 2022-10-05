package com.pad.Users;

import com.orbitz.consul.NotRegisteredException;
import com.pad.Users.service.GrpcService;
import com.pad.Users.service.ServiceRegistration;
import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class GrpcServerImpl implements GrpcServer{

    @Autowired
    GrpcService grpcService;

    @EventListener(ApplicationReadyEvent.class)
    @Override
    public void run() throws IOException, InterruptedException, NotRegisteredException {
        Server grpcServer = ServerBuilder.forPort(80).addService((BindableService) grpcService).build();
        grpcServer.start();

        log.info("gRPC server started!");
        ServiceRegistration.registerService();

        grpcServer.awaitTermination();
    }
}
