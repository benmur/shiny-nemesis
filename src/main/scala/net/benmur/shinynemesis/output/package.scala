/**
  * Copyright (c) 2012-2013 Rached Ben Mustapha
  *
  * See the file LICENSE for copying permission.
  */

package net.benmur.shinynemesis

import java.io.File

import net.benmur.shinynemesis.analyzer.{ ColorSupport, ImageAnalyzer }

package object output {
  trait Reporter[T] {
    type ColorSequence = net.benmur.shinynemesis.analyzer.ColorSequence

    def writeStatsFrom(analyzer: ImageAnalyzer with ColorSupport) = {
      writeOut(renderDoc(analyzer.colors))
      println("Wrote to " + filename)
    }

    protected def renderDoc(colors: ColorSequence): T
    protected def writeOut(doc: T)
    protected def filename: String

    def alreadyExists: Boolean = new File(filename).exists
  }
}
