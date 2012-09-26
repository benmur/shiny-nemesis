/**
  * Copyright (c) 2012 Rached Ben Mustapha
  *
  * See the file LICENSE for copying permission.
  */

package net.benmur.frameprint.output

import java.awt.{Color, GradientPaint, Graphics2D}
import java.awt.image.BufferedImage
import java.io.File

import javax.imageio.ImageIO
import net.benmur.frameprint.Config
import net.benmur.frameprint.analyzer.{ColorQuantity, ColorSupport, ImageAnalyzer}

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
    val h = Config.OUTPUT_IMAGE_HEIGHT
    val h2 = Config.OUTPUT_COLOR2_HEIGHT
    val image = new BufferedImage(colors size, h, BufferedImage.TYPE_INT_ARGB)
    val g = image.createGraphics()

    colors.zipWithIndex map {
      case ((c1, c2), i) =>
        c1 map { c => drawColor(g, i, 0, h, c, c) }
        c2 map { c => drawColor(g, i, h - h2, h2, transparent(c), c) }
    }

    g.dispose()

    image
  }

  private def drawColor(graphics: Graphics2D, x: Int, y: Int, height: Int, color1: Color, color2: Color) = {
    val paint = new GradientPaint(0, y, color1, 0, y + height, color2);
    graphics.setPaint(paint)
    graphics.drawLine(x, y, x, y + height - 1)
  }

  private implicit def colorQuantity2Color(cq: ColorQuantity): Color =
    new Color(cq.r, cq.g, cq.b)

  private def transparent(c: Color): Color =
    new Color(c.getRed, c.getGreen, c.getBlue, 0)

  private def writeOut(image: BufferedImage, output: String) = ImageIO.write(image, "png", new File(output))
}