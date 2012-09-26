/**
  * Copyright (c) 2012 Rached Ben Mustapha
  *
  * See the file LICENSE for copying permission.
  */

package net.benmur.frameprint.input.xuggle

import scala.annotation.tailrec

import com.xuggle.mediatool.MediaListenerAdapter
import com.xuggle.mediatool.event.IVideoPictureEvent
import com.xuggle.xuggler.{ IContainer, IStream }

import net.benmur.frameprint.Config
import net.benmur.frameprint.analyzer.{ ImageAnalyzer, Picture }

class XuggleFrameListener(val imageAnalyzer: ImageAnalyzer, val container: IContainer) extends MediaListenerAdapter {
  private var lastPrint = System.currentTimeMillis()
  private var frames = 0
  private var totalFrames = 0

  var eofDetected = false

  override def onVideoPicture(event: IVideoPictureEvent): Unit = {
    countFrames(event)
    imageAnalyzer receive event
    seek(event)
  }

  private def countFrames(event: IVideoPictureEvent) = {
    totalFrames += 1
    frames += 1
    val now = System.currentTimeMillis()
    if (now - lastPrint >= 2000) {
      println("ts=" + formatTs(event.getTimeStamp()) + " fps=~" + frames / 2 + " total=" + totalFrames)
      lastPrint = now
      frames = 0
    }
  }

  private def formatTs(ts: Long): String = {
    val tsSeconds = ts / 1000000
    val seconds = tsSeconds % 60
    val minutes = (tsSeconds / 60) % 60
    val hours = (tsSeconds / 3600) % 60
    "%02d:%02d:%02d".format(hours, minutes, seconds)
  }

  implicit private def toPicture(e: IVideoPictureEvent): Picture = new XuggleBufferedImagePictureImpl(e.getImage())

  private def seek(event: IVideoPictureEvent) = {
    val streamIndex = event.getStreamIndex()
    val stream = container.getStream(streamIndex toLong)
    val frameInterval = stream.getNumFrames().toInt / Config.TOTAL_FRAMES
    nextKeyFrame(stream, frameInterval * totalFrames) map { i =>
      val time = stream.getIndexEntry(i).getTimeStamp()
      container.seekKeyFrame(streamIndex, time, 0)
    }
  }

  @tailrec
  private def nextKeyFrame(stream: IStream, index: Int): Option[Int] =
    if (index >= stream.getNumIndexEntries()) {
      Some(stream.getNumIndexEntries() - 1)
    } else if (index == stream.getNumIndexEntries() - 1) {
      eofDetected = true
      None
    } else
      stream.getIndexEntry(index) match {
        case e if e.isKeyFrame() => Some(index)
        case _                   => nextKeyFrame(stream, index + 1)
      }
}