package com.van1164.user.subscribe.service

import com.van1164.common.domain.UserSubscribe
import com.van1164.common.exception.AlreadySubscribeException
import com.van1164.user.UserRepository
import com.van1164.user.subscribe.repository.SubscribeRepository
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toMono

@Service
class SubscribeService(
    private val subscribeRepository: SubscribeRepository,
    private val userRepository: UserRepository
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
            .flatMap {
                subscribeRepository.save(it)
            }
            .map {
                ResponseEntity.ok().body("success")
            }
            .onErrorResume {
                when(it){
                    is NotFoundException -> ResponseEntity.badRequest().body("존재하지 않는 User").toMono()
                    is AlreadySubscribeException -> ResponseEntity.badRequest().body("이미 구독 중").toMono()
                    else ->  ResponseEntity.badRequest().body("fail").toMono()
                }
            }
    }

    @Transactional
    fun subscribeCancel(fromUserId: String, toUserId: String): Mono<ResponseEntity<String>> {
        return subscribeRepository.findByFromUserIdAndToUserId(fromUserId,toUserId)
            .doOnNext {
                println(it)
            }
            .flatMap {
                subscribeRepository.delete(it)
            }
            .thenReturn(ResponseEntity.ok().body("success"))
            .defaultIfEmpty(ResponseEntity.badRequest().body("존재하지 않는 User"))
            .onErrorReturn(ResponseEntity.badRequest().body("fail"))
    }
}