package com.example.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;

public class GreeterClient {

    public static void main(String[] args) throws InterruptedException {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        GreeterServiceGrpc.GreeterServiceBlockingStub stub =
                GreeterServiceGrpc.newBlockingStub(channel);

        // --- 단방향 RPC ---
        System.out.println("=== SayHello (Unary) ===");
        HelloRequest request = HelloRequest.newBuilder()
                .setName("World")
                .build();

        System.out.println("[Client] SayHello request:\n" + request);
        HelloReply reply = stub.sayHello(request);
        System.out.println("[Client] SayHello reply:\n" + reply);

        // --- 서버 스트리밍 RPC ---
        System.out.println("\n=== SayHelloStream (Server Streaming) ===");
        HelloRequest streamRequest = HelloRequest.newBuilder()
                .setName("Kotlin")
                .setTimes(5)
                .build();

        System.out.println("[Client] SayHelloStream request:\n" + streamRequest);
        Iterator<HelloReply> responses = stub.sayHelloStream(streamRequest);
        int i = 1;
        while (responses.hasNext()) {
            HelloReply r = responses.next();
            System.out.println("[Client] SayHelloStream reply #" + i++ + ":\n" + r);
        }

        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        System.out.println("\nChannel closed.");
    }
}
