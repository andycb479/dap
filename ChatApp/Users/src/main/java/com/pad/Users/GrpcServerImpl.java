package com.pad.Users;

import com.pad.Users.service.UsersGrpcService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class GrpcServerImpl implements GrpcServer{
    @Override
    public void run() throws IOException, InterruptedException {
        Server grpcServer = ServerBuilder.forPort(8083).addService(new UsersGrpcService()).build();
        grpcServer.start();

        log.info("gRPC server started!");

        grpcServer.awaitTermination();
    }
}
