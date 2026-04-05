package com.example.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;

@Slf4j
public class GreeterClient {

    public static void main(String[] args) throws InterruptedException {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        GreeterServiceGrpc.GreeterServiceBlockingStub stub =
                GreeterServiceGrpc.newBlockingStub(channel);

        // --- 단방향 RPC ---
        log.info("=== SayHello (Unary) ===");
        HelloRequest request = HelloRequest.newBuilder()
                .setName("World")
                .build();

        log.info(">>> [CLIENT → SERVER] SayHello request:\n{}", request);
        HelloReply reply = stub.sayHello(request);
        log.info("<<< [CLIENT ← SERVER] SayHello reply:\n{}", reply);

        // --- 서버 스트리밍 RPC ---
        log.info("=== SayHelloStream (Server Streaming) ===");
        HelloRequest streamRequest = HelloRequest.newBuilder()
                .setName("Kotlin")
                .setTimes(5)
                .build();

        log.info(">>> [CLIENT → SERVER] SayHelloStream request:\n{}", streamRequest);
        Iterator<HelloReply> responses = stub.sayHelloStream(streamRequest);
        int i = 1;
        while (responses.hasNext()) {
            HelloReply r = responses.next();
            log.info("<<< [CLIENT ← SERVER] SayHelloStream reply #{}:\n{}", i++, r);
        }

        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        log.info("Channel closed.");
    }
}
