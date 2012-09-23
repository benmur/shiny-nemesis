/**
 * Copyright (c) 2012 Rached Ben Mustapha
 *
 * See the file LICENSE for copying permission.
 */

package net.benmur.frameprint.input.xuggle

import java.awt.image.BufferedImage

import net.benmur.frameprint.analyzer.Picture

class XuggleBufferedImagePictureImpl(val i: BufferedImage) extends Picture {
  override def eachPixel(block: Int => Unit) =
    0 until i.getWidth() foreach { x =>
      0 until i.getHeight() foreach { y =>
        block(i.getRGB(x, y))
      }
    }
}