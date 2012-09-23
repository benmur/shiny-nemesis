/**
 * Copyright (c) 2012 Rached Ben Mustapha
 *
 * See the file LICENSE for copying permission.
 */

package net.benmur.frameprint

package object input {
  sealed trait ReadStatus
  case object Eof extends ReadStatus
  case object Error extends ReadStatus

  trait Reader {
    def readAll(): ReadStatus
  }
}