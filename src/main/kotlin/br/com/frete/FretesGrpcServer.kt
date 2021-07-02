package br.com.frete

import com.google.protobuf.Any
import com.google.rpc.Code
import com.google.rpc.ErrorDetailsProto

import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.grpc.protobuf.StatusProto
import io.grpc.stub.StreamObserver
import org.slf4j.LoggerFactory
import java.lang.IllegalStateException
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class FretesGrpcServer : FretesServiceGrpc.FretesServiceImplBase() {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun calculaFrete(request: CalculaFreteRequest?, responseObserver: StreamObserver<CalculaFreteResponse>?) {
        logger.info("Calculando frete para ${request}")

        val cep = request!!.cep
        if(cep == null || cep.isBlank()){
            val e = Status.INVALID_ARGUMENT.withDescription("O CEP deve ser informado")
                .asRuntimeException()
            responseObserver?.onError(e)
        }

        if(!cep!!.matches("[0-9]{5}-[0-9]{3}".toRegex())){
            val e = Status.INVALID_ARGUMENT.withDescription("O CEP informado é inválido")
                .asRuntimeException()
            responseObserver?.onError(e)
        }

        if(cep.endsWith("001")){
            val statusProto = com.google.rpc.Status.newBuilder()
                .setCode(Code.PERMISSION_DENIED.number)
                .setMessage("O usuário não pode acessar este recurso")
                .addDetails(
                    Any.pack(ErrorDetails.newBuilder()
                    .setCode(401)
                    .setMessage("Token expirado")
                    .build()
                ))
                .build()

            val e = StatusProto.toStatusRuntimeException(statusProto)
            responseObserver?.onError(e)
        }

        var valor = 0.0

        try{
            valor = Random.nextDouble(from = 0.0, until = 140.0)
            if(valor > 100.0){
                throw IllegalStateException("Erro inesperado ao executar a lógica de negócio!")
            }
        }catch (e : Exception){
            responseObserver?.onError(Status.INTERNAL
                .withDescription(e.message)
                .withCause(e)
                .asRuntimeException())
        }


        val response = CalculaFreteResponse.newBuilder()
            .setCep(request!!.cep)
            .setValor(valor)
            .build()

        logger.info("Frete calculado: ${response}")

        responseObserver!!.onNext(response)
        responseObserver.onCompleted()
    }
}