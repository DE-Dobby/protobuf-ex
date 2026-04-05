package com.example.grpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class GreeterServer {

    private static final int PORT = 50051;

    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = ServerBuilder.forPort(PORT)
                .addService(new GreeterServiceImpl())
                .build()
                .start();

        log.info("Server started on port {}", PORT);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Shutting down server...");
            server.shutdown();
        }));

        server.awaitTermination();
    }

    @Slf4j
    static class GreeterServiceImpl extends GreeterServiceGrpc.GreeterServiceImplBase {

        // 단방향 RPC
        @Override
        public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
            log.info("<<< [SERVER ← CLIENT] SayHello request:\n{}", request);

            HelloReply reply = HelloReply.newBuilder()
                    .setMessage("Hello, " + request.getName() + "!")
                    .build();

            log.info(">>> [SERVER → CLIENT] SayHello reply:\n{}", reply);
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }

        // 서버 스트리밍 RPC
        @Override
        public void sayHelloStream(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
            log.info("<<< [SERVER ← CLIENT] SayHelloStream request:\n{}", request);

            int times = request.getTimes() > 0 ? request.getTimes() : 3;
            for (int i = 1; i <= times; i++) {
                HelloReply reply = HelloReply.newBuilder()
                        .setMessage("Hello #" + i + ", " + request.getName() + "!")
                        .build();
                log.info(">>> [SERVER → CLIENT] SayHelloStream reply #{}:\n{}", i, reply);
                responseObserver.onNext(reply);

                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            responseObserver.onCompleted();
        }
    }
}
