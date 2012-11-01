/**
  * Copyright (c) 2012 Rached Ben Mustapha
  *
  * See the file LICENSE for copying permission.
  */

package net.benmur.shinynemesis

import java.io.File

import net.benmur.shinynemesis.analyzer.ColorAnalyzer
import net.benmur.shinynemesis.input.{ Eof, Error }
import net.benmur.shinynemesis.input.xuggle.XuggleReader
import net.benmur.shinynemesis.output.ImageColorOutput

object ShinyNemesis extends App {
  args foreach { file =>
    val outputFile = new File(file).getName().replaceAll("\\.[\\d\\w]+$", ".png")
    val out = new ImageColorOutput(outputFile)
    if (!out.alreadyExists) {
      val colorAnalyzer = new ColorAnalyzer(Config.COLOR_DIFF_THRESHOLD, 1)
      println("Reading " + file)
      new XuggleReader(file, colorAnalyzer).readAll match {
        case Error =>
          println("Finishing unexpectedly, not writing output")
        case Eof =>
          println("End of file reached, writing to " + outputFile)
          out.writeStatsFrom(colorAnalyzer)
      }
    }
  }
  println("Took %ds".format((System.currentTimeMillis() - executionStart) / 1000))
}