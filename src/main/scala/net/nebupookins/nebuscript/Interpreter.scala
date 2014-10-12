package net.nebupookins.nebuscript

import java.io.File

import scala.io.Source
import scala.util.parsing.combinator.lexical.Lexical
import scala.util.parsing.combinator.token.StdTokens
import scala.util.parsing.input._
import com.typesafe.scalalogging.LazyLogging

case class CommandLineArgs(
  file: File = null
)

object NebuScript extends LazyLogging {

  def main(args: Array[String]) {
    logger.info("Parsing command line arguments...")
    val argsParser = new scopt.OptionParser[CommandLineArgs]("NebuScript") {
      head("NebuScript", "0.1")
      arg[File]("<file>") action { (f, c) =>
        c.copy(file = f)
      } text "The name of the NebuScript file to run."
    }
    argsParser.parse(args, CommandLineArgs()) map { config =>
      val source = Source.fromFile(config.file)
      logger.info(s"Reading source file ${source}...")
      val fileContents = source.getLines().mkString("\n")
      source.close()
      logger.info("Reading code...")
      val codeReader = IgnoresCommonMarkReader(new CharSequenceReader(fileContents))
      var codeReaderTemp: Reader[Char] = codeReader
      logger.info("Lexing...")
      val lexer = NebuLexer(codeReader)
      var parseResults = NebuParser(lexer)
      parseResults match {
        case NebuParser.Success(result, next) =>
          println(result) //TODO
        case NebuParser.NoSuccess(parser) =>
          println(parser)
      }
    }
  }
}

