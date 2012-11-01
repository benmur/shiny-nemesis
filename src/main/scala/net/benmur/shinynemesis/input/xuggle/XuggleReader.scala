/**
  * Copyright (c) 2012 Rached Ben Mustapha
  *
  * See the file LICENSE for copying permission.
  */

package net.benmur.shinynemesis.input.xuggle

import java.awt.image.BufferedImage

import scala.annotation.tailrec

import com.xuggle.mediatool.ToolFactory

import net.benmur.shinynemesis.Config.MAX_READS
import net.benmur.shinynemesis.analyzer.ImageAnalyzer
import net.benmur.shinynemesis.input.{ Eof, Error, ReadStatus, Reader }

class XuggleReader(val file: String, val analyzer: ImageAnalyzer)
    extends Reader {

  val xreader = ToolFactory.makeReader(file)
  val frameListener = new XuggleFrameListener(analyzer, xreader.getContainer())
  xreader.setBufferedImageTypeToGenerate(BufferedImage.TYPE_3BYTE_BGR)
  xreader.addListener(frameListener)

  @tailrec
  private def read(packet: Int): ReadStatus = xreader.readPacket() match {
    case null =>
      if (maxNotReached(packet) && !frameListener.eofDetected)
        read(packet + 1)
      else
        shutdown(Eof)
    case error if error.getErrorNumber() == -541478725 =>
      shutdown(Eof)
    case error =>
      println("Error reading packet: " + error)
      shutdown(Error)
  }

  private def shutdown(status: ReadStatus): ReadStatus = {
    analyzer.finish()
    status
  }

  private def maxNotReached(packet: Int) = (MAX_READS > 0 && packet <= MAX_READS) || MAX_READS < 0

  override def readAll = try {
    read(0)
  } catch {
    case e: RuntimeException =>
      e.printStackTrace()
      Error
  } finally {
    xreader.close()
  }
}
