/**
 * Copyright (c) 2012 Rached Ben Mustapha
 *
 * See the file LICENSE for copying permission.
 */

package net.benmur.frameprint.analyzer

import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.ListMap
import scala.collection.mutable.Map

import net.benmur.frameprint.Config

class ColorAnalyzer(
  private val tolerance: Int,
  private val frameGroupSize: Int)
  extends ImageAnalyzer with ColorSupport {

  private var currentFrame = 0
  private var currentFrameMap = Map[Int, Int]()
  private val finalMaps: ArrayBuffer[(Option[ColorQuantity], Option[ColorQuantity])] =
    ArrayBuffer[(Option[ColorQuantity], Option[ColorQuantity])]()

  override def finish() = {
    rotateColorMap()
  }

  override def frameGroups = finalMaps size
  override def colorSpreadMap(frameGroup: Int): (Option[ColorQuantity], Option[ColorQuantity]) = finalMaps(frameGroup)

  private def cleanupMap(map: scala.collection.Map[Int, Int]) = {
    val top2 = map.
      toList.
      sortBy(_._2).
      takeRight(2).
      map(toQuantity)
    (top2 headOption, if (top2.isEmpty) None else top2.tail headOption)
  }

  private def toQuantity(kv: (Int, Int)): ColorQuantity = kv match {
    case (k, v) =>
      val (r, g, b) = intToRGB(k)
      ColorQuantity(r, g, b, v)
  }

  private def intToRGB(rgb: Int): (Int, Int, Int) =
    ((rgb & 0xff0000) >> 16, (rgb & 0x00ff00) >> 8, (rgb & 0x0000ff))

  private def isColorfulEnough(r: Int, g: Int, b: Int): Boolean =
    (r - g).abs > tolerance || (r - b).abs > tolerance

  private def isBrightEnough(r: Int, g: Int, b: Int): Boolean =
    brightness(r, g, b) > Config.MIN_BRIGHTNESS

  private def brightness(r: Int, g: Int, b: Int): Double =
    0.299 * r + 0.587 * g + 0.114 * b

  private def flattenColor(rgb: Int): Int = rgb & 0xf0f0f0

  private def rotateColorMap() = {
    finalMaps += cleanupMap(currentFrameMap)
    currentFrameMap = Map[Int, Int]()
  }

  override def receive(pic: Picture) = {
    if (currentFrame % frameGroupSize == 0 && !currentFrameMap.isEmpty) {
      rotateColorMap()
    }
    currentFrame += 1

    pic eachPixel { rgb =>
      val flattened = flattenColor(rgb)
      val (r, g, b) = intToRGB(rgb)

      if (isColorfulEnough(r, g, b) && isBrightEnough(r, g, b)) {
        currentFrameMap(flattened) = currentFrameMap.getOrElseUpdate(flattened, 0) + 1
      }
    }
  }
}
