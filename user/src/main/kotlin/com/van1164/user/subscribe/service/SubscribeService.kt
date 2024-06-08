package com.van1164.user.subscribe.service

import com.van1164.common.domain.UserSubscribe
import com.van1164.common.exception.AlreadySubscribeException
import com.van1164.common.redis.RedisService
import com.van1164.user.UserRepository
import com.van1164.user.subscribe.repository.SubscribeRepository
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.TransactionManager
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.reactive.TransactionalOperator
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toMono
import java.lang.Exception

@Service
class SubscribeService(
    private val subscribeRepository: SubscribeRepository,
    private val userRepository: UserRepository,
    private val redisService: RedisService,
    private val transactionalOperator: TransactionalOperator,
) {

    @Transactional
    fun subscribe(fromUserId: String, toUserId: String): Mono<ResponseEntity<String>> {
        return userRepository.findFirstByUserId(fromUserId)
            .switchIfEmpty{Mono.error(NotFoundException())}
            .flatMap {
                userRepository.findFirstByUserId(toUserId)
            }
            .switchIfEmpty{Mono.error(NotFoundException())}
            .flatMap {
                subscribeRepository.existsByFromUserIdAndToUserId(fromUserId,toUserId)
            }
            .filter{it == false}
            .switchIfEmpty { Mono.error(AlreadySubscribeException()) }
            .map{
                UserSubscribe(fromUserId,toUserId)
            }
            .flatMap(subscribeRepository::save)
            .flatMap {
                redisService.increaseSubscribe(it.toUserId)
            }
            .map {
                ResponseEntity.ok().body("success")
            }
            .`as`(transactionalOperator::transactional)
            .onErrorResume {
                when(it){
                    is NotFoundException -> ResponseEntity.badRequest().body("존재하지 않는 User").toMono()
                    is AlreadySubscribeException -> ResponseEntity.badRequest().body("이미 구독 중").toMono()
                    else ->  ResponseEntity.internalServerError().body("fail").toMono()
                }
            }
    }

    @Transactional
    fun subscribeCancel(fromUserId: String, toUserId: String): Mono<ResponseEntity<String>> {
        return subscribeRepository.findByFromUserIdAndToUserId(fromUserId,toUserId)
            .switchIfEmpty { Mono.error(NotFoundException()) }
            .flatMap {
                subscribeRepository.delete(it)
            }
            .then(Mono.defer { redisService.decreaseSubscribe(toUserId) })
            .thenReturn(ResponseEntity.ok().body("success"))
            .switchIfEmpty(Mono.error(Exception()))
            .onErrorResume {
                when(it){
                    is NotFoundException -> ResponseEntity.badRequest().body("구독중이 아닙니다.").toMono()
                    else ->  ResponseEntity.internalServerError().body("fail").toMono()
                }
            }

    }
}