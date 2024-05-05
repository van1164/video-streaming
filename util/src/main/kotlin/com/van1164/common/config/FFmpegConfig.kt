package com.van1164.common.config

import net.bramp.ffmpeg.FFmpeg
import net.bramp.ffmpeg.FFprobe
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FFmpegConfig(
    @Value("\${ffmpegPath}")
    val ffmepgPath : String,

    @Value("\${ffprobePath}")
    val ffprobePath : String
) {

    @Bean
    fun getFFmpeg() : FFmpeg{
        return FFmpeg(ffmepgPath)
    }

    @Bean
    fun getFFprobe() : FFprobe{
        return FFprobe(ffprobePath)
    }
}