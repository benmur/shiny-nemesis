/**
  * Copyright (c) 2012 Rached Ben Mustapha
  *
  * See the file LICENSE for copying permission.
  */

package net.benmur.frameprint

import java.io.File

import net.benmur.frameprint.analyzer.{ ColorAnalyzer, ColorSupport, ImageAnalyzer }
import net.benmur.frameprint.input.{ Eof, Error }
import net.benmur.frameprint.input.xuggle.XuggleReader
import net.benmur.frameprint.output.ImageColorOutput

object FramePrint extends App {
  args foreach { file =>
    val colorAnalyzer = new ColorAnalyzer(
      Config.COLOR_DIFF_THRESHOLD, 1)

    new XuggleReader(file, colorAnalyzer).readAll match {
      case Error =>
        println("Finishing unexpectedly, not writing output")
      case Eof =>
        new ImageColorOutput(new File(file).getName().replaceAll("\\.[\\d\\w]+$", ".png")).writeStatsFrom(colorAnalyzer)
    }
  }
  println("Took %ds".format((System.currentTimeMillis() - executionStart) / 1000))
}