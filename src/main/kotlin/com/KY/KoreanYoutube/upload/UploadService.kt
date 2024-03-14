package com.KY.KoreanYoutube.upload

import com.KY.KoreanYoutube.domain.Video
import com.KY.KoreanYoutube.upload.dto.UploadVideoPartDTO
import com.KY.KoreanYoutube.video.VideoRepository
import net.bramp.ffmpeg.FFmpeg
import net.bramp.ffmpeg.FFmpegExecutor
import net.bramp.ffmpeg.FFprobe
import net.bramp.ffmpeg.builder.FFmpegBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date
import java.util.UUID

@Service
class UploadService(
    val uploadRepository: UploadRepository,
    val videoRepository: VideoRepository,
    @Value("\${aws.s3.bucketUrl}")
    val bucketUrl: String,
    val ffmpeg: FFmpeg,
    val ffprobe: FFprobe
) {
    fun uploadVideoPart(video: MultipartFile, videoData: UploadVideoPartDTO): ResponseEntity<Any> {

        uploadRepository.uploadVideoPart(video, videoData.chunkNumber)



        if (videoData.totalChunk - 1 == videoData.chunkNumber) {
            //여러 part를 하나의 파일로 만들기
            val inputFilePath = Paths.get(UUID.randomUUID().toString() + ".mp4")
            Files.createFile(inputFilePath)
            for (i: Int in 0 until videoData.totalChunk) {
                val videoPart = uploadRepository.getPart(bucketUrl, video.originalFilename, i) ?: return ResponseEntity(
                    HttpStatus.BAD_REQUEST
                )
                Files.write(inputFilePath, videoPart.readAllBytes(), StandardOpenOption.APPEND)
                uploadRepository.deletePart(video.originalFilename, i)
            }

            //m3u8과 ts들로 변경해야함
            val outputUUID = UUID.randomUUID().toString()
            val m3u8Path = "$outputUUID.m3u8"
            println(inputFilePath.toString())

            //thumbnail by ffmpeg
            val thumbNailPath = UUID.randomUUID().toString() + ".jpg"
            extractThumbnail(inputFilePath.toString(), thumbNailPath)

            //uploadThumbnail
            uploadRepository.uploadThumbnail(thumbNailPath)

            //mp4 to ts
            mp4ToM3U8(inputFilePath, m3u8Path, outputUUID)

//            //ts 분할
//            divideTsFile(tsFilePath)

            // 여러 TS들을 S3에 업로드
            uploadRepository.uploadVideoTs(outputUUID)
            uploadRepository.uploadM3U8(m3u8Path)
            println("https://video-stream-spring.s3.ap-northeast-2.amazonaws.com/$m3u8Path")

            videoRepository.save(
                Video(
                    createDate = Date.from(
                        LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()
                    ), id = outputUUID, title = videoData.title, thumbNailUrl = thumbNailPath
                )
            )

            return ResponseEntity(outputUUID, HttpStatus.OK)
        } else {
            return ResponseEntity(HttpStatus.PARTIAL_CONTENT)   
        }
    }

    private fun extractThumbnail(inputFilePath: String, thumbNailPath: String) {
        FFmpegBuilder()
            .setInput(inputFilePath)
            .addOutput(thumbNailPath)
            .addExtraArgs("-ss", "00:00:1")
            .addExtraArgs("-vframes", "1")
            .setStrict(FFmpegBuilder.Strict.EXPERIMENTAL)
            .done()
            .apply {
                FFmpegExecutor(ffmpeg, ffprobe).createJob(this).run()
            }

    }

//    private fun divideTsFile(tsFilePath: String) {
//        val segmentBuilder =
//            FFmpegBuilder().setInput(tsFilePath)
//                .addOutput("${tsFilePath}_%03d.ts")
//                .addExtraArgs("-c", "copy")
//                .addExtraArgs("-map", "0")
//                .addExtraArgs("-segment_time", "5")
//                .addExtraArgs("-f", "segment")
//                .addExtraArgs("-reset_timestamps", "1")
//                .setStrict(FFmpegBuilder.Strict.EXPERIMENTAL)
//                .done()
//        FFmpegExecutor(ffmpeg, ffprobe).createJob(segmentBuilder).run()
//        File(tsFilePath).delete()
//    }

    private fun mp4ToM3U8(inputFilePath: Path, m3u8Path: String, tsFilePath: String) {
        val builder = FFmpegBuilder()
            .setInput(inputFilePath.toString())
            .addOutput(m3u8Path)
            .addExtraArgs("-c", "copy")
            .addExtraArgs("-bsf:v", "h264_mp4toannexb")
            .addExtraArgs("-hls_segment_filename", "${tsFilePath}_%03d.ts")
            .addExtraArgs("-start_number", "0")
            .addExtraArgs("-hls_time", "5")
            .addExtraArgs("-hls_list_size", "0")
            .addExtraArgs("-hls_base_url", "https://video-stream-spring.s3.ap-northeast-2.amazonaws.com/")
            .addExtraArgs("-f", "hls")
            .setStrict(FFmpegBuilder.Strict.EXPERIMENTAL).done()
        FFmpegExecutor(ffmpeg, ffprobe).createJob(builder).run()
        File(inputFilePath.toString()).delete()
    }
}