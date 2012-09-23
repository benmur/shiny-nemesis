/**
 * Copyright (c) 2012 Rached Ben Mustapha
 *
 * See the file LICENSE for copying permission.
 */

package net.benmur.frameprint.analyzer

trait Picture {
  def eachPixel(block: Int => Unit)
}