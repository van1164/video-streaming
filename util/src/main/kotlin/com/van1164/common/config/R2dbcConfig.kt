package com.van1164.common.config

import io.r2dbc.spi.ConnectionFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import org.springframework.r2dbc.connection.R2dbcTransactionManager
import org.springframework.transaction.ReactiveTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.transaction.annotation.TransactionManagementConfigurer
import org.springframework.transaction.reactive.TransactionalOperator


@Configuration
@EnableR2dbcRepositories
@EnableTransactionManagement
class R2dbcConfig(private val connectionFactory: ConnectionFactory) : TransactionManagementConfigurer {
    @Bean
    fun transactionManager(): ReactiveTransactionManager {
        return R2dbcTransactionManager(connectionFactory)
    }

    @Bean
    fun transactionalOperator(transactionManager: ReactiveTransactionManager): TransactionalOperator {
        return TransactionalOperator.create(transactionManager)
    }

    override fun annotationDrivenTransactionManager(): ReactiveTransactionManager {
        return transactionManager()
    }
}