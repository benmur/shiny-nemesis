/**
  * Copyright (c) 2012 Rached Ben Mustapha
  *
  * See the file LICENSE for copying permission.
  */

package net.benmur.frameprint

package object analyzer {
  case class ColorQuantity(r: Int, g: Int, b: Int, quantity: Int)

  trait ImageAnalyzer {
    def receive(pic: Picture): Unit
    def frameGroups: Int
    def finish(): Unit
  }

  trait ColorSupport {
    def colorSpreadMap(frameGroup: Int): (Option[ColorQuantity], Option[ColorQuantity])
  }
}