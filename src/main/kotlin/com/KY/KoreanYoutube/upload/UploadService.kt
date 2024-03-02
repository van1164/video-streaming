package com.KY.KoreanYoutube.upload

import com.KY.KoreanYoutube.upload.dto.UploadVideoPartDTO
import net.bramp.ffmpeg.FFmpeg
import net.bramp.ffmpeg.FFmpegExecutor
import net.bramp.ffmpeg.FFprobe
import net.bramp.ffmpeg.builder.FFmpegBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.util.UUID

@Service
class UploadService(
    val uploadRepository: UploadRepository,
    @Value("\${aws.s3.bucketUrl}")
    val bucketUrl: String,
    val ffmpeg: FFmpeg,
    val ffprobe: FFprobe
) {
    fun uploadVideoPart(video: MultipartFile, videoData: UploadVideoPartDTO): ResponseEntity<Any> {

        uploadRepository.uploadVideoPart(video, videoData.chunkNumber)



        if (videoData.totalChunk - 1 == videoData.chunkNumber) {
            val inputFilePath = Paths.get(UUID.randomUUID().toString() + ".mp4")
            Files.createFile(inputFilePath)



            for (i: Int in 0 until videoData.totalChunk) {
                val videoPart = uploadRepository.getPart(bucketUrl, video.originalFilename, i) ?: return ResponseEntity(
                    HttpStatus.BAD_REQUEST
                )
                Files.write(inputFilePath, videoPart.readAllBytes(), StandardOpenOption.APPEND)
                uploadRepository.deletePart(video.originalFilename, i)
            }
            //이제 ts로 변경 필요
            val tsFilePath = UUID.randomUUID().toString() + ".ts"
            println(inputFilePath.toString())

            //mp4 to ts
            val builder = FFmpegBuilder()
                .setInput(inputFilePath.toString())
                .addOutput(tsFilePath).addExtraArgs("-c","copy")
                .addExtraArgs("-bsf:v","h264_mp4toannexb")
                .addExtraArgs("-f", "mpegts")
                .setStrict(FFmpegBuilder.Strict.EXPERIMENTAL).done()
            FFmpegExecutor(ffmpeg, ffprobe).createJob(builder).run()

            //ts 분할
            val segmentBuilder =
                FFmpegBuilder().setInput(tsFilePath)
                    .addOutput("${tsFilePath}_%03d.ts")
                    .addExtraArgs("-c", "copy")
                    .addExtraArgs("-map", "0")
                    .addExtraArgs("-segment_time", "5")
                    .addExtraArgs("-f", "segment")
                    .addExtraArgs("-reset_timestamps", "1")
                    .setStrict(FFmpegBuilder.Strict.EXPERIMENTAL)
                    .done()
            FFmpegExecutor(ffmpeg, ffprobe).createJob(segmentBuilder).run()
            return ResponseEntity(HttpStatus.OK)
        } else {
            return ResponseEntity(HttpStatus.PARTIAL_CONTENT)
        }
    }
}