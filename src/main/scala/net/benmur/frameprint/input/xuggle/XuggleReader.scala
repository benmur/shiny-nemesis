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
import net.benmur.frameprint.input.Eof
import net.benmur.frameprint.input.ReadStatus
import net.benmur.frameprint.input.Reader

class XuggleReader(val file: String, val analyzer: ImageAnalyzer)
  extends Reader {

  val xreader = ToolFactory.makeReader(file)
  xreader.setQueryMetaData(false)
  xreader.setBufferedImageTypeToGenerate(BufferedImage.TYPE_3BYTE_BGR)
  xreader.addListener(new XuggleFrameListener(analyzer))

  @tailrec
  private def read(packet: Int): ReadStatus = xreader.readPacket() match {
    case null => if ((MAX_READS > 0 && packet <= MAX_READS) || MAX_READS < 0) read(packet + 1) else Eof
    case error => println(error); Eof
  }

  override def readAll = try {
    read(0)
  } finally {
    xreader.close()
  }
}