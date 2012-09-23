/**
 * Copyright (c) 2012 Rached Ben Mustapha
 *
 * See the file LICENSE for copying permission.
 */

package net.benmur.frameprint

import net.benmur.frameprint.analyzer.ColorAnalyzer
import net.benmur.frameprint.input.Eof
import net.benmur.frameprint.input.xuggle.XuggleReader
import net.benmur.frameprint.output.HtmlColorReporter
import net.benmur.frameprint.output.ImageColorOutput

object FramePrint {
  def main(args: Array[String]): Unit = {
    args foreach { file =>
      val colorAnalyzer = new ColorAnalyzer(Config.COLOR_DIFF_THRESHOLD, 25)
      val reader = new XuggleReader(file, colorAnalyzer)

      reader.readAll match {
        case Eof => println("got eof")
        case e => println("_ got " + e)
      }

      new HtmlColorReporter(file + ".html").writeStatsFrom(colorAnalyzer)
      new ImageColorOutput(file + ".png").writeStatsFrom(colorAnalyzer)
    }
  }
}