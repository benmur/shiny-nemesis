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
  private val frameGroupSize: Int,
  private val endOfWorkAction: (ImageAnalyzer with ColorSupport) => Unit)
  extends ImageAnalyzer with ColorSupport {

  private var currentFrame = 0
  private var currentFrameMap = Map[Int, Int]()
  private val finalMaps: ArrayBuffer[scala.collection.Map[(Int, Int, Int), Int]] = ArrayBuffer[scala.collection.Map[(Int, Int, Int), Int]]()

  override def finish() = {
    println("color analyzer finishing")
    rotateColorMap()
    endOfWorkAction(this)
  }

  override def frameGroups = finalMaps size
  override def colorSpreadMap(frameGroup: Int): scala.collection.Map[(Int, Int, Int), Int] = finalMaps(frameGroup)

  private def cleanupMap(map: scala.collection.Map[Int, Int]) =
    ListMap(map.toList.
      sortBy(_._2).
      takeRight(1).
      map(convert): _*)

  private def convert(kv: (Int, Int)): ((Int, Int, Int), Int) = kv match {
    case (k, v) => (intToRGB(k), v)
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
