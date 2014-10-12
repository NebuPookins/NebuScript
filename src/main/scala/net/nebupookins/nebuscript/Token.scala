package net.nebupookins.nebuscript

import scala.util.parsing.input.Position

case class TokenWithPosition(token: Token, position: Position)

sealed abstract class Token
case class Identifier(image: String) extends Token
case class StringLiteral(value: String) extends Token
case class ErrorToken(image: String) extends Token
case class Keyword(name: String) extends Token
case class Punctuation(image: String) extends Token
case class EOFToken() extends Token