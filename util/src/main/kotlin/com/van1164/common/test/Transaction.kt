package com.van1164.common.test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.ReactiveTransaction
import org.springframework.transaction.reactive.TransactionalOperator
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class Transaction @Autowired constructor(val rxtx: TransactionalOperator) {

    fun <T> withRollback(publisher: Mono<T>): Mono<T> {
        return rxtx.execute { tx: ReactiveTransaction ->
            tx.setRollbackOnly()
            publisher
        }
            .next()
    }

    fun <T> withRollback(publisher: Flux<T>): Flux<T> {
        return rxtx.execute { tx: ReactiveTransaction ->
            tx.setRollbackOnly()
            publisher
        }
    }

}