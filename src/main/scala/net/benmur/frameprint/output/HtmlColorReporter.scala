/**
 * Copyright (c) 2012 Rached Ben Mustapha
 *
 * See the file LICENSE for copying permission.
 */

package net.benmur.frameprint.output

import java.io.BufferedWriter
import java.io.FileWriter
import scala.xml.NodeSeq
import net.benmur.frameprint.analyzer.ColorSupport
import net.benmur.frameprint.analyzer.ImageAnalyzer
import net.benmur.frameprint.analyzer.ColorQuantity

class HtmlColorReporter(val outputFile: String) extends Reporter {
  override def writeStatsFrom(analyzer: ImageAnalyzer with ColorSupport) = {
    println("writing to " + outputFile)
    val colors = 0 until analyzer.frameGroups flatMap { group =>
      println("  frame group " + group + "... ")
      analyzer.colorSpreadMap(group) match {
        case (q1, q2) => q1 map toHex
      }
    }

    val nodeSeq = renderDoc(colors)
    val output = outputFile.replaceAll("/", "_")
    writeOut(nodeSeq, output)

    println("wrote to " + outputFile)
  }

  private def toHex(rgb: ColorQuantity) = {
    "%02x%02x%02x".format(rgb.r, rgb.g, rgb.b)
  }

  private def writeOut(nodes: NodeSeq, output: String) {
    val writer = new BufferedWriter(new FileWriter(output))
    writer.write(nodes.toString())
    writer.close()
  }

  private def renderDoc(colors: Seq[String]) =
    <html>
      <body>
        {
          colors map { c =>
            <div style={ "height: 1px; background-color: #" + c + ";" }>&nbsp;</div>
          }
        }
      </body>
    </html>
}