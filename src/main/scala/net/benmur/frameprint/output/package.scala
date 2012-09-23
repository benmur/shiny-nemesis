/**
 * Copyright (c) 2012 Rached Ben Mustapha
 *
 * See the file LICENSE for copying permission.
 */

package net.benmur.frameprint

import net.benmur.frameprint.analyzer.ColorSupport
import net.benmur.frameprint.analyzer.ImageAnalyzer

package object output {
  trait Reporter {
    def writeStatsFrom(analyzer: ImageAnalyzer with ColorSupport)
  }
}