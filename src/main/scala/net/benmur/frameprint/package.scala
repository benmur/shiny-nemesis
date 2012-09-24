/**
 * Copyright (c) 2012 Rached Ben Mustapha
 *
 * See the file LICENSE for copying permission.
 */

package net.benmur

package object frameprint {
  object Config {
    val MAX_READS = -1
    val ANALYZE_EVERY_NTH = 8
    val FRAME_GROUP_SIZE = 25

    val MIN_BRIGHTNESS = 50
    val COLOR_DIFF_THRESHOLD = 20

    val OUTPUT_IMAGE_HEIGHT = 32
    val OUTPUT_COLOR2_HEIGHT = 8
  }
}