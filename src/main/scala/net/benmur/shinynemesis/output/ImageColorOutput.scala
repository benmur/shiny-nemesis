/**
  * Copyright (c) 2012-2013 Rached Ben Mustapha
  *
  * See the file LICENSE for copying permission.
  */

package net.benmur.shinynemesis.output

import java.awt.{ Color, GradientPaint, Graphics2D }
import java.awt.image.BufferedImage
import java.io.File

import javax.imageio.ImageIO
import net.benmur.shinynemesis.Config
import net.benmur.shinynemesis.analyzer.ColorQuantity

class ImageColorOutput(val filename: String) extends Reporter[BufferedImage] {
  protected def renderDoc(colors: ColorSequence) = {
    val h = Config.OUTPUT_IMAGE_HEIGHT
    val h2 = Config.OUTPUT_COLOR2_HEIGHT
    val image = new BufferedImage(colors.size, h, BufferedImage.TYPE_INT_ARGB)
    val g = image.createGraphics()

    colors.zipWithIndex foreach {
      case ((c1, c2), i) =>
        c1 map { c =>
          new Color(c.r, c.g, c.b)
        } foreach { color =>
          drawColor(g, i, 0, h, color, color)
        }
        c2 map { c =>
          new Color(c.r, c.g, c.b)
        } foreach { color =>
          drawColor(g, i, h - h2, h2, transparent(color), color)
        }
    }

    g.dispose()

    image
  }

  override protected def writeOut(image: BufferedImage) = ImageIO.write(image, "png", new File(filename))

  private def drawColor(graphics: Graphics2D, x: Int, y: Int, height: Int, color1: Color, color2: Color) = {
    val paint = new GradientPaint(0, y, color1, 0, y + height, color2);
    graphics.setPaint(paint)
    graphics.drawLine(x, y, x, y + height - 1)
  }

  private def transparent(c: Color): Color =
    new Color(c.getRed, c.getGreen, c.getBlue, 0)
}
