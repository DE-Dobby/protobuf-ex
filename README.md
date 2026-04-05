# protobuf-ex

gRPC와 Protocol Buffers를 활용한 Java 예제 프로젝트입니다.  
단방향(Unary) RPC와 서버 스트리밍(Server Streaming) RPC를 구현합니다.

## 프로젝트 구조

```
protobuf-ex/
├── pom.xml                                          # Maven 빌드 설정 (gRPC, Protobuf 의존성)
└── src/
    └── main/
        ├── proto/
        │   └── greeter.proto                        # Protobuf 서비스 및 메시지 정의
        └── java/com/example/grpc/
            ├── GreeterServer.java                   # gRPC 서버 (포트 50051)
            └── GreeterClient.java                   # gRPC 클라이언트
```

## 주요 구성 요소

### `greeter.proto`
`GreeterService`를 정의합니다.

| RPC | 종류 | 설명 |
|-----|------|------|
| `SayHello` | Unary | 이름을 받아 인사 메시지 1건 반환 |
| `SayHelloStream` | Server Streaming | 이름과 횟수를 받아 인사 메시지를 `times`번 스트리밍 |

**메시지**
- `HelloRequest` — `name` (string), `times` (int32)
- `HelloReply` — `message` (string)

### `GreeterServer.java`
포트 `50051`에서 gRPC 서버를 실행합니다.  
`GreeterServiceImplBase`를 상속해 두 RPC를 구현하며, 서버 스트리밍 시 응답 간 300ms 딜레이를 둡니다.  
요청/응답마다 Protobuf 메시지 전체 내용을 로그로 출력합니다.

```
[Server] SayHello request:
name: "World"

[Server] SayHello reply:
message: "Hello, World!"

[Server] SayHelloStream request:
name: "Kotlin"
times: 5

[Server] SayHelloStream reply #1:
message: "Hello #1, Kotlin!"
```

### `GreeterClient.java`
`localhost:50051`에 연결해 두 RPC를 순서대로 호출합니다.  
요청/응답마다 Protobuf 메시지 전체 내용을 로그로 출력합니다.

```
=== SayHello (Unary) ===
[Client] SayHello request:
name: "World"

[Client] SayHello reply:
message: "Hello, World!"

=== SayHelloStream (Server Streaming) ===
[Client] SayHelloStream request:
name: "Kotlin"
times: 5

[Client] SayHelloStream reply #1:
message: "Hello #1, Kotlin!"
```

> Protobuf `toString()`은 필드명과 값을 텍스트 포맷으로 출력하며, 기본값(0, 빈 문자열 등)인 필드는 생략됩니다.

## 실행 방법

```bash
# 빌드
mvn clean package

# 서버 실행
mvn exec:java -Dexec.mainClass=com.example.grpc.GreeterServer

# 클라이언트 실행 (별도 터미널)
mvn exec:java -Dexec.mainClass=com.example.grpc.GreeterClient
```