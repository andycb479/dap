package com.pad.Gateway.services;

import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public interface GrpcServer {
  void run() throws IOException, InterruptedException;
}
