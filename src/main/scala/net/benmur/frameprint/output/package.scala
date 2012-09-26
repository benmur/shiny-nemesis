/**
  * Copyright (c) 2012 Rached Ben Mustapha
  *
  * See the file LICENSE for copying permission.
  */

package net.benmur.frameprint

import java.io.File

import net.benmur.frameprint.analyzer.{ ColorSupport, ImageAnalyzer }

package object output {
  trait Reporter[T] {
    type ColorSequence = net.benmur.frameprint.analyzer.ColorSequence

    def writeStatsFrom(analyzer: ImageAnalyzer with ColorSupport) = {
      writeOut(renderDoc(analyzer colors))
      println("Wrote to " + filename)
    }

    protected def renderDoc(colors: ColorSequence): T
    protected def writeOut(doc: T)
    protected def filename: String

    def alreadyExists: Boolean = new File(filename).exists
  }
}