package net.nebupookins.nebuscript

import scala.util.parsing.combinator.Parsers
import scala.util.parsing.input.Reader

object NebuParser extends Parsers {
  type Elem = TokenWithPosition

  private def compilationUnit: Parser[CompilationUnit] =
    namespace.*  ^^ {case ns => CompilationUnit(ns)}

  private def namespace: Parser[Namespace] =
    ???

  def apply(in: Reader[TokenWithPosition]): ParseResult[CompilationUnit] =
    compilationUnit(in)
}
