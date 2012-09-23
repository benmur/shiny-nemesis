/**
 * Copyright (c) 2012 Rached Ben Mustapha
 *
 * See the file LICENSE for copying permission.
 */

package net.benmur.frameprint.output

import net.benmur.frameprint.analyzer.ColorSupport
import net.benmur.frameprint.analyzer.ImageAnalyzer
import java.io.FileWriter
import java.awt.image.BufferedImage
import net.benmur.frameprint.Config
import java.io.File
import javax.imageio.ImageIO
import java.awt.Color

class ImageColorOutput(val outputFile: String) extends Reporter {

  def writeStatsFrom(analyzer: ImageAnalyzer with ColorSupport): Unit = {
    println("writing to " + outputFile)
    val colors = 0 until analyzer.frameGroups flatMap { group =>
      println("  frame group " + group + "... ")
      analyzer.colorSpreadMap(group)
    }

    val image = createImage(colors)
    val output = outputFile.replaceAll("/", "_")
    writeOut(image, output)

    println("wrote to " + outputFile)
  }

  private def createImage(colors: Seq[((Int, Int, Int), Int)]): BufferedImage = {
    val image = new BufferedImage(colors size, Config.OUTPUT_IMAGE_HEIGHT, BufferedImage.TYPE_3BYTE_BGR)
    val gr = image.createGraphics()

    colors.zipWithIndex map {
      case (((r, g, b), w), i) =>
        gr.setColor(new Color(r, g, b))
        gr.drawLine(i, 0, i, Config.OUTPUT_IMAGE_HEIGHT - 1)
    }

    image
  }

  private def writeOut(image: BufferedImage, output: String) = ImageIO.write(image, "png", new File(output))
}