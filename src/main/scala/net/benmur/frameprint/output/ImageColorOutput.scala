/**
 * Copyright (c) 2012 Rached Ben Mustapha
 *
 * See the file LICENSE for copying permission.
 */

package net.benmur.frameprint.output

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File

import javax.imageio.ImageIO
import net.benmur.frameprint.Config
import net.benmur.frameprint.analyzer.ColorQuantity
import net.benmur.frameprint.analyzer.ColorSupport
import net.benmur.frameprint.analyzer.ImageAnalyzer

class ImageColorOutput(val outputFile: String) extends Reporter {

  def writeStatsFrom(analyzer: ImageAnalyzer with ColorSupport): Unit = {
    val output = outputFile.replaceAll("/", "_")
    println("writing to " + output)
    val colors = 0 until analyzer.frameGroups map (analyzer.colorSpreadMap)
    val image = createImage(colors)
    writeOut(image, output)
    println("wrote to " + output)
  }

  private def createImage(colors: Seq[(Option[ColorQuantity], Option[ColorQuantity])]): BufferedImage = {
    val image = new BufferedImage(colors size, Config.OUTPUT_IMAGE_HEIGHT, BufferedImage.TYPE_3BYTE_BGR)
    val gr = image.createGraphics()

    colors.zipWithIndex map {
      case ((c1, c2), i) =>
        c1 map {
          case ColorQuantity(r1, g1, b1, w1) =>
            gr.setColor(new Color(r1, g1, b1))
            gr.drawLine(i, 0, i, Config.OUTPUT_IMAGE_HEIGHT - 1)
        }
        c2 map {
          case ColorQuantity(r2, g2, b2, w2) =>
            val c2Start = (Config.OUTPUT_IMAGE_HEIGHT - Config.OUTPUT_COLOR2_HEIGHT) / 2 - 1
            gr.setColor(new Color(r2, g2, b2))
            gr.drawLine(i, c2Start, i, c2Start + Config.OUTPUT_COLOR2_HEIGHT)
        }
    }

    image
  }

  private def writeOut(image: BufferedImage, output: String) = ImageIO.write(image, "png", new File(output))
}