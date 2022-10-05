package com.pad.Users;

import com.orbitz.consul.NotRegisteredException;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public interface GrpcServer {
    void run() throws IOException, InterruptedException, NotRegisteredException;
}
