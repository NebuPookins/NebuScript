package net.nebupookins.nebuscript

sealed abstract class AstNode

case class CompilationUnit(namespaces: List[Namespace]) extends AstNode
case class Namespace(name: String) extends AstNode