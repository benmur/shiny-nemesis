/**
 * Copyright (c) 2012 Rached Ben Mustapha
 *
 * See the file LICENSE for copying permission.
 */

package net.benmur.frameprint.input.xuggle

import scala.actors.Actor

import com.xuggle.mediatool.MediaListenerAdapter
import com.xuggle.mediatool.event.IVideoPictureEvent
import com.xuggle.xuggler.IContainer

import net.benmur.frameprint.analyzer.ImageAnalyzer
import net.benmur.frameprint.analyzer.Picture

class XuggleFrameListener(val imageAnalyzer: ImageAnalyzer, val container: IContainer) extends MediaListenerAdapter {
  private var lastPrint = System.currentTimeMillis()
  private var frames = 0
  private var totalFrames = 0

  private val analyzerActor = new AnalyzerProxy(imageAnalyzer)
  analyzerActor.start

  override def onVideoPicture(event: IVideoPictureEvent): Unit = {
    countFrames(event)
    analyzerActor ! QueueImage(event)
    container.seekKeyFrame(event.getStreamIndex(), 150 * totalFrames, IContainer.SEEK_FLAG_FRAME)
  }

  def eof() = analyzerActor ! EndOfWork

  def countFrames(event: IVideoPictureEvent) = {
    totalFrames += 1
    frames += 1
    val now = System.currentTimeMillis()
    if (now - lastPrint >= 1000) {
      println("got picture, ts=" + event.getTimeStamp() + " fps=~" + frames + " total=" + totalFrames)
      lastPrint = now
      frames = 0
    }
  }

  implicit def toPicture(e: IVideoPictureEvent): Picture = new XuggleBufferedImagePictureImpl(e.getImage())

  case class QueueImage(p: Picture)
  case object EndOfWork

  class AnalyzerProxy(val analyzer: ImageAnalyzer) extends Actor {
    def act() {
      while (true) {
        receive {
          case QueueImage(p) =>
            analyzer receive p
          case EndOfWork =>
            analyzer.finish()
            exit()
        }
      }
    }
  }
}