NebuScript is a high-level, general purpose, functional, literate, type-oriented,
contracts-first, statically-typed programming language. It is inspired by
Scala, Haskell, Clojure and Java. It is syntactically a subset of CommonMark,
meaning that all valid NebuScript files are valid CommonMark files. The file
you are reading right now is a valid NebuScript file.

# Current Project Status

Not fit for public consumption. Interpreter is still being developed; lexing
works but AST isn't fully built yet. Details of the language could change at
any time.

# Philosophy and design goals

## High-level, General Purpose

NebuScript is suitable for the development of desktop applications,
server-side web apps, as a shell scripting language, etc.

It might not be suitable for low level system programming, device drivers,
or for real-time/embedded systems.

**TODO**: There is currently no interop system defined, though of course it
would be extremely useful to be able to, for example, interoperate with the
JVM to gain access to a huge list of library code. How to provide this
interop without sacrificing the other qualities of NebuScript is an area of
active research.

## Functional

NebuScript embraces two core principles from functional programming:

* Functions are first-class values that can be stored in variables, passed in
  as arguments, returned as values, etc. There is a convenient Lambda syntax
  for defining anonymous functions.
* Functions are pure: They have no side effects and are referentially
  transparent. Programmers programming in NebuScript are encouraged to use
  monads to implement code that would normally rely upon side effects.

NebuScript is not a *pure* functional language: sometimes a piece of code can
be expressed more simply if it, for example, relies on modifying state.
NebuScript provides facilities for writing impure code, and provides
facilities to limit the scope of the impurities. In particular, the
type-checker will prevent pure code from invoking impure code.

## Literate

NebuScript believes that source code is written primarily for humans to read,
and only secondarily for computers to read. Code must be well-documented in
order to be reusable. Therefore, NebuScript was designed to make documentating
code as painless as possible.

In a typical NebuScript program, the amount of bytes/characters devoted to
documentation will likely be greater than the amount of bytes/characters
devoted to code.

It has always been a fundamental principle of programming that things that
vary-together should be encapsulated together. Where most other programming
languages have fallen short was in dealing with the coupling between
requirements and code. When the requirements change, the code has to change.
So why haven't we encapsulated the requirements and the code together?

In other programming languages, it is often difficult to understand the
(e.g. business) motivation behind a given piece of code. If you were lucky,
there may be a comment summarizing why the code does what it does. If you were
luckier, the comment may have contained a link to an internal wiki or bug
tracker containing the requirements. In NebuScript, because it is a literate
programing language, you write the requirements (e.g. in plain English), and
then immediately next to those requirements, you write the code that
implements those requirements.

Given that the requirements are written in plain English (or whatever your
team's native natural language is), your business users can actually read a
NebuScript file and participate in finding bugs (e.g. by stating that the
requirements do not match their expectation).

## Type-oriented, Contracts-First

NebuScript is type-oriented, rather than object-oriented or class-oriented. A
NebuScript program declares types (in the Liskov Substitution Principle sense
of the word). There is no concept of classes, so NebuScript is definitely not
class-oriented. There is the concept of objects, however because many
programmers associate the term "Object Oriented" to imply the existence and
reliance on classes, we avoid describing NebuScript as being Object Oriented.

A *type* defines a set of "allowed" values, as well as a set of operations on
those values. These operations have contracts defined upon them, and the
type-checker will ensure that the appropriate covariant/contravariant rules
are obeyed when one type is declared to be a subtype of another type.

An object can have multiple types simultaneously (e.g. 3 is simultaneously an
Integer and a Number), but there is no inheritance of behaviour, only
inheritance of contracts. Re-use of behaviour is better implemented via a
has-a relationship, or via delegation, than via an is-a relationship.

## Statically-typed

NebuScript strongly believes in enabling a collaboration between the human
programmer and the compiler when designing programs. Modern software systems
are too large to keep in an unaugmented human mind. Ensuring that the type of
every parameter and return value is known at compile time allows you to catch
bugs earlier, allows the compiler to verify your assumptions, and acts as
a form of documentation for fellow human programmers.

# Overview

In this section, we will take various (language agnostic) exercises and show
how they would be implemented in NebuScript.

As mentioned earlier:

* NebuScript is a literate programming language.
* Every valid NebuScript file is a valid CommonMark file.
* This file you're reading right now is a valid NebuScript file.

In practice, this is implemented by having the NebuScript compiler ignore all
lines which neither starts with a tab character nor starts with 4 space
characters. In other words, the executable parts of a NebuScript file are
contained in CommonMark's indented code blocks.

## Bob

This exercise is taken from http://exercism.io/

    namespace Bob {

Bob is a lackadaisical teenager. In conversation, his responses are very
limited.

Bob answers 'Sure.' if you ask him a question.

      tests(
        (
          "Does this cryogenic chamber make me look fat?",
          "You are what, like 15?",
          "4?",
          "Wait! Hang on. Are you going to be OK?"
        ).all?(λ(message) = respond(message) == "Sure.")
      )

He answers 'Whoa, chill out!' if you yell at him.

      tests(
        (
          "WATCH OUT!",
          "WHAT THE HELL WERE YOU THINKING?",
          "1, 2, 3, GO!",
          "I HATE YOU"
        ).all?(λ(message) = respond(message) == "Whoa, chill out!")
      )

He says 'Fine. Be that way!' if you address him without actually saying
anything.

      tests(
        (
          "",
          "        ",
        ).all?(λ(message) = respond(message) == "Fine. Be that way!")
      )

He answers 'Whatever.' to anything else.

      tests(
        (
          "Tom-ay-to, tom-aaaah-to.",
          "Let's go work out at the gym!",
          "It's OK if you don't want to go to the DMV.",
          "1, 2, 3",
          "Ending with a ? means a question.",
          "Does this cryogenic chamber make me look fat?
          no"
        ).all?(λ(message) = respond(message) == "Whatever.")
      )

      function respond(message: String): String =
        if question?(message)
          "Sure."
        else if yelling?(message)
          "Whoa, chill out!"
        else if silence?(message)
          "Fine. Be that way!"
        else
          "Whatever."

      function question?(message: String): Boolean =
        message.endsWith?("?")

      function yelling?(message: String): Boolean =
        message.all?(lambda(c) = not(c.lowercase?()))

      function silence?(message: String): Boolean =
        message.blank?()
    }

