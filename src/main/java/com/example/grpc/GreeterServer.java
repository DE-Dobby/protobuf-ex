package com.example.grpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;

public class GreeterServer {

    private static final int PORT = 50051;

    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = ServerBuilder.forPort(PORT)
                .addService(new GreeterServiceImpl())
                .build()
                .start();

        System.out.println("Server started on port " + PORT);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down server...");
            server.shutdown();
        }));

        server.awaitTermination();
    }

    static class GreeterServiceImpl extends GreeterServiceGrpc.GreeterServiceImplBase {

        // 단방향 RPC
        @Override
        public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
            System.out.println("[Server] SayHello request:\n" + request);

            HelloReply reply = HelloReply.newBuilder()
                    .setMessage("Hello, " + request.getName() + "!")
                    .build();

            System.out.println("[Server] SayHello reply:\n" + reply);
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }

        // 서버 스트리밍 RPC
        @Override
        public void sayHelloStream(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
            System.out.println("[Server] SayHelloStream request:\n" + request);

            int times = request.getTimes() > 0 ? request.getTimes() : 3;
            for (int i = 1; i <= times; i++) {
                HelloReply reply = HelloReply.newBuilder()
                        .setMessage("Hello #" + i + ", " + request.getName() + "!")
                        .build();
                System.out.println("[Server] SayHelloStream reply #" + i + ":\n" + reply);
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
