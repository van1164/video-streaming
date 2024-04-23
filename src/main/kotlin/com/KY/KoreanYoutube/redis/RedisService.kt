package com.KY.KoreanYoutube.redis

import com.KY.KoreanYoutube.domain.User
import com.KY.KoreanYoutube.utils.RTMP_ING_PREFIX
import com.KY.KoreanYoutube.utils.RTMP_PREFIX
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Service
import java.time.Duration


@Service
class RedisService(
    private val redisRepository: RedisRepository,
    private val mapper: ObjectMapper
) {

    fun saveRtmp(streamKey : String){
        redisRepository.save(RTMP_PREFIX + streamKey,"off",Duration.ofMinutes(10))
    }

    fun loadRtmp(streamKey: String): String? {
        return redisRepository.load(RTMP_PREFIX + streamKey,String::class.java)
    }

    fun loadRtmpAndRemove(streamKey: String): String? {
        return redisRepository.removeRtmp(RTMP_PREFIX + streamKey)
    }


    fun saveJwt(jwt: String, user: User) {
        redisRepository.save(jwt,mapper.writeValueAsString(user),Duration.ofHours(3))
    }

    fun loadByJwt(jwt: String): User? {
        return redisRepository.load(jwt, User::class.java)
    }

    fun saveRtmpIng(key: String) {
        redisRepository.save(RTMP_ING_PREFIX + key,"live",Duration.ofHours(12))
    }

    fun doneRtmpIng(streamKey: String) {
        redisRepository.removeRtmp(RTMP_ING_PREFIX +streamKey)
    }



}