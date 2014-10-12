package net.nebupookins.nebuscript

import com.typesafe.scalalogging.LazyLogging

import scala.util.parsing.input.Reader

/**
 * Given the raw contents of a NebuScript file, returns a Reader that iterates
 * over the intended code blocks, ignoring the other CommonMark stuff.
 */
case class IgnoresCommonMarkReader(in: Reader[Char], atBeginningOfLine: Boolean = true) extends Reader[Char] with LazyLogging {
  val positionedReader = if (atBeginningOfLine) IgnoresCommonMarkReader.advanceToCodeBlock(in) else in

  def atEnd = positionedReader.atEnd

  def first = positionedReader.first

  def pos = positionedReader.pos

  def rest = if (atEnd) {
    this
  } else {
    val next = positionedReader.rest.first
    if (next == '\r' || next == '\n') {
      IgnoresCommonMarkReader(positionedReader.rest, true)
    } else {
      IgnoresCommonMarkReader(positionedReader.rest, false)
    }
  }
}

object IgnoresCommonMarkReader extends LazyLogging {
  /**
   * Assumes you're at the start of a line.
   */
  private def advanceToCodeBlock(r: Reader[Char]): Reader[Char] = {
    if (r.atEnd) {
      r
    } else if (r.first == '\t') {
      r.rest
    } else if (fourSpaces(r)) {
      r.drop(4)
    } else {
      var temp = r
      do {
        temp = temp.rest
      } while (!temp.atEnd && temp.first != '\n' && temp.first != '\r')
      while (!temp.atEnd && (temp.first == '\n' || temp.first == '\r')) {
        temp = temp.rest
      }
      advanceToCodeBlock(temp)
    }
  }

  private def fourSpaces(r1: Reader[Char]): Boolean = {
    if (r1.first != ' ') {
      false
    } else {
      val r2 = r1.rest
      if (r2.first != ' ') {
        false
      } else {
        val r3 = r2.rest
        if (r3.first != ' ') {
          false
        } else {
          val r4 = r3.rest
          r4.first == ' '
        }
      }
    }
  }
}