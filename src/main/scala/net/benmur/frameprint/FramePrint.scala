/**
 * Copyright (c) 2012 Rached Ben Mustapha
 *
 * See the file LICENSE for copying permission.
 */

package net.benmur.frameprint

import net.benmur.frameprint.analyzer.ColorAnalyzer
import net.benmur.frameprint.analyzer.ColorSupport
import net.benmur.frameprint.analyzer.ImageAnalyzer
import net.benmur.frameprint.input.Eof
import net.benmur.frameprint.input.xuggle.XuggleReader
import net.benmur.frameprint.output.ImageColorOutput

object FramePrint {
  def main(args: Array[String]): Unit = {
    args foreach { file =>
      val colorAnalyzer = new ColorAnalyzer(
        Config.COLOR_DIFF_THRESHOLD, 1,
        (c: ImageAnalyzer with ColorSupport) => {
          new ImageColorOutput(file + ".png").writeStatsFrom(c)
        })

      val reader = new XuggleReader(file, colorAnalyzer)

      reader.readAll match {
        case Eof => println("got eof")
        case e => println("_ got " + e)
      }
    }
  }
}