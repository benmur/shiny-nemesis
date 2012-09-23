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

class ColorAnalyzer(val tolerance: Int, val frameGroupSize: Int)
  extends ImageAnalyzer with ColorSupport {
  var currentFrame = 0
  val maps: ArrayBuffer[Map[Int, Int]] = ArrayBuffer[Map[Int, Int]]()

  override def frameGroups = maps size
  override def colorSpreadMap(frameGroup: Int): scala.collection.Map[(Int, Int, Int), Int] = ListMap(
    maps(frameGroup).
      toList.
      sortBy(_._2).
      map(convert).
      takeRight(1): _*)

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

  private def store(rgb: Int, map: Map[Int, Int]) = {
    map(rgb) = map.getOrElseUpdate(rgb, 0) + 1
  }

  override def receive(pic: Picture) = {
    if (currentFrame % frameGroupSize == 0) {
      maps += Map[Int, Int]()
    }
    currentFrame += 1

    val frameMap = maps.last
    pic eachPixel { rgb =>
      val (r, g, b) = intToRGB(rgb)

      if (isColorfulEnough(r, g, b) && isBrightEnough(r, g, b)) {
        frameMap(rgb) = frameMap.getOrElseUpdate(rgb, 0) + 1
      }
    }
  }
}
