/**
  * Copyright (c) 2012 Rached Ben Mustapha
  *
  * See the file LICENSE for copying permission.
  */

package net.benmur.frameprint.input.xuggle

import java.awt.image.BufferedImage

import scala.annotation.tailrec

import com.xuggle.mediatool.ToolFactory

import net.benmur.frameprint.Config.MAX_READS
import net.benmur.frameprint.analyzer.ImageAnalyzer
import net.benmur.frameprint.input.{Eof, Error, ReadStatus, Reader}

class XuggleReader(val file: String, val analyzer: ImageAnalyzer)
    extends Reader {

  val xreader = ToolFactory.makeReader(file)
  val frameListener = new XuggleFrameListener(analyzer, xreader.getContainer())
  xreader.setBufferedImageTypeToGenerate(BufferedImage.TYPE_3BYTE_BGR)
  xreader.addListener(frameListener)

  @tailrec
  private def read(packet: Int): ReadStatus = xreader.readPacket() match {
    case null =>
      if ((MAX_READS > 0 && packet <= MAX_READS) || MAX_READS < 0)
        read(packet + 1)
      else
        shutdown(Eof)
    case error if error.getErrorNumber() == -541478725 =>
      println("End of file reached")
      shutdown(Eof)
    case error =>
      println("Error reading packet: " + error)
      shutdown(Error)
  }

  private def shutdown(status: ReadStatus): ReadStatus = {
    analyzer.finish()
    status
  }

  override def readAll = try {
    read(0)
  } finally {
    xreader.close()
  }
}
