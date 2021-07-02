package br.com.frete

import io.grpc.health.v1.HealthCheckRequest
import io.grpc.health.v1.HealthCheckResponse
import io.grpc.health.v1.HealthGrpc
import io.grpc.stub.StreamObserver
import javax.inject.Singleton

/*
  rpc Check(HealthCheckRequest) returns (HealthCheckResponse);
    é uma chamada simples request/response, pode pingar no servidor e ver o estado dele

  rpc Watch(HealthCheckRequest) returns (stream HealthCheckResponse);
    stream do lado servidor, o cliente vai abrir um canal de stream com o servidor e vai ficar escutando qual é o estado da aplicação
 */

@Singleton
class HealthCheckerService : HealthGrpc.HealthImplBase() {
    override fun check(request: HealthCheckRequest?, responseObserver: StreamObserver<HealthCheckResponse>?) {
        responseObserver?.onNext(HealthCheckResponse.newBuilder()
                                    .setStatus(HealthCheckResponse.ServingStatus.SERVING)
                                    .build()
                                )
        responseObserver?.onCompleted()
    }

    override fun watch(request: HealthCheckRequest?, responseObserver: StreamObserver<HealthCheckResponse>?) {
        responseObserver?.onNext(HealthCheckResponse.newBuilder()
                                    .setStatus(HealthCheckResponse.ServingStatus.SERVING)
                                    .build()
                                )

    }
}