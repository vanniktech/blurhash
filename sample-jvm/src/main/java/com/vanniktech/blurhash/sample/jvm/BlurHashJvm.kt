package com.vanniktech.blurhash.sample.jvm

import org.jcodec.api.FrameGrab
import org.jcodec.api.specific.AVCMP4Adaptor
import org.jcodec.common.SeekableDemuxerTrack
import org.jcodec.containers.mp4.demuxer.MP4Demuxer
import org.jcodec.scale.AWTUtil
import java.io.File
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

@OptIn(ExperimentalTime::class) fun main() {
  val directory = File("sample-jvm/images/").takeIf { it.exists() } ?: File("images/")
  val video = directory.resolve("video-1098x1952.mp4")

  val channel = SeekableInMemoryByteChannel(video.inputStream().readBytes())
  MP4Demuxer.createMP4Demuxer(channel).use { createMP4Demuxer ->
    val videoTrack = createMP4Demuxer.videoTrack as SeekableDemuxerTrack
    val frameGrab = FrameGrab(videoTrack, AVCMP4Adaptor(videoTrack.meta)).seekToSecondPrecise(0.0)
    val picture = frameGrab.nativeFrame
    println(picture.width) // Prints 1104 but should be 1098.
    println(picture.height) // Prints 1952 which is correct.
  }
}
