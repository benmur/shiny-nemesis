/**
 * Copyright (c) 2012 Rached Ben Mustapha
 *
 * See the file LICENSE for copying permission.
 */

package net.benmur.frameprint.input.xuggle

import scala.actors.Actor

import com.xuggle.mediatool.MediaListenerAdapter
import com.xuggle.mediatool.event.IVideoPictureEvent

import net.benmur.frameprint.Config.ANALYZE_EVERY_NTH
import net.benmur.frameprint.analyzer.ImageAnalyzer
import net.benmur.frameprint.analyzer.Picture

class XuggleFrameListener(val imageAnalyzer: ImageAnalyzer) extends MediaListenerAdapter {
  private var lastPrint = System.currentTimeMillis()
  private var frames = 0
  private var totalFrames = 0

  private val analyzerActor = new AnalyzerProxy(imageAnalyzer)
  analyzerActor.start

  override def onVideoPicture(event: IVideoPictureEvent): Unit = {
    countFrames(event)

    totalFrames += 1
    if (totalFrames % ANALYZE_EVERY_NTH == 0) {
      analyzerActor ! QueueImage(event)
    }
  }

  def eof() = analyzerActor ! EndOfWork

  def countFrames(event: IVideoPictureEvent) = {
    frames += 1
    val now = System.currentTimeMillis()
    if (now - lastPrint >= 1000) {
      println("got picture, ts=" + event.getTimeStamp() + " fps=~" + frames)
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