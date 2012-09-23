/**
 * Copyright (c) 2012 Rached Ben Mustapha
 *
 * See the file LICENSE for copying permission.
 */

package net.benmur.frameprint

package object analyzer {
  trait ImageAnalyzer {
    def receive(pic: Picture): Unit
    def frameGroups: Int
  }

  trait ColorSupport {
    def colorSpreadMap(frameGroup: Int): scala.collection.Map[(Int, Int, Int), Int]
  }
}