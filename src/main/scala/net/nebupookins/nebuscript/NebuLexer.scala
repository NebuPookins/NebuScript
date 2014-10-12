package net.nebupookins.nebuscript

import scala.util.parsing.combinator.Parsers
import scala.util.parsing.combinator.lexical.Lexical
import scala.util.parsing.input.{CharArrayReader, Position, Reader}


/**
 * Converts a stream of characters into a stream of tokens
 */
case class NebuLexer(in: Reader[Char]) extends Reader[TokenWithPosition] {
  /** Convenience constructor (makes a character reader out of the given string) */
  def this(in: String) = this(new CharArrayReader(in.toCharArray()))

  private val (tok, rest1, rest2) = NebuLexer.whitespace(in) match {
    case NebuLexer.Success(_, firstNonWhitespace) =>
      NebuLexer.token(firstNonWhitespace) match {
        case NebuLexer.Success(token, afterToken) => (TokenWithPosition(token, firstNonWhitespace.pos), firstNonWhitespace, afterToken)
        case ns: NebuLexer.NoSuccess =>
          if (ns.next.atEnd)
            (TokenWithPosition(EOFToken(), firstNonWhitespace.pos), ns.next, skip(ns.next)) //TODO: Is this a hack?
          else
            (TokenWithPosition(ErrorToken("2" +ns.msg), firstNonWhitespace.pos), ns.next, skip(ns.next))
      }
    case ns: NebuLexer.NoSuccess => (TokenWithPosition(ErrorToken(ns.msg), in.pos), ns.next, skip(ns.next))
  }

  private def skip(in: Reader[Char]) = if (in.atEnd) in else in.rest

  override def source: java.lang.CharSequence = in.source
  override def offset: Int = in.offset
  def first = tok
  def rest = new NebuLexer(rest2)
  def pos = rest1.pos
  def atEnd = in.atEnd || (NebuLexer.whitespace(in) match { case NebuLexer.Success(_, in1) => in1.atEnd case _ => false })
}

object NebuLexer extends Lexical {
  val keywords = Set(
    "namespace"
  )
  type Token = net.nebupookins.nebuscript.Token

  def identifierHead: Parser[Elem] = letter | elem('_')

  def identifierBody: Parser[Elem] = identifierHead | digit | elem('?')

  def token: Parser[Token] =
    identifierOrKeyword |
    '\"' ~ rep( chrExcept('\"', '\032')) ~ '\"' ^^ { case '\"' ~ chars ~ '\"' =>
      StringLiteral(chars.mkString(""))
    } |
    punctuation

  def identifierOrKeyword: Parser[Token] =
    identifierHead ~ rep(identifierBody) ^^ { case first ~ rest =>
      val image = (first :: rest).mkString("")
      if (keywords.contains(image)) {
        Keyword(image)
      } else {
        Identifier(image)
      }
    }


  def punctuation: Parser[Punctuation] =
    (elem('{') | '}' | '(' | ')' | ',' | '.' | '=' | '!') ^^ {case image => Punctuation(image.toString)}

  def whitespace: Parser[Any] = rep(whitespaceChar)
}
