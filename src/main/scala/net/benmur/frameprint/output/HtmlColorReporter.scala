/**
  * Copyright (c) 2012 Rached Ben Mustapha
  *
  * See the file LICENSE for copying permission.
  */

package net.benmur.frameprint.output

import java.io.{ BufferedWriter, FileWriter }

import scala.Option.option2Iterable
import scala.xml.NodeSeq

import net.benmur.frameprint.analyzer.ColorQuantity

class HtmlColorReporter(val filename: String) extends Reporter[NodeSeq] {
  override protected def writeOut(nodes: NodeSeq) {
    val writer = new BufferedWriter(new FileWriter(filename))
    writer.write(nodes.toString())
    writer.close()
  }

  override protected def renderDoc(colors: ColorSequence) =
    <html>
      <body>
        {
          colors flatMap {
            _._1.map(toHex).map { cc =>
              <div style={ "height: 1px; background-color: #" + cc + ";" }>&nbsp;</div>
            }
          }
        }
      </body>
    </html>

  private def toHex(rgb: ColorQuantity) = {
    "%02x%02x%02x".format(rgb.r, rgb.g, rgb.b)
  }
}