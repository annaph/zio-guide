package org.zio.guide.testing

import zio.{Console, IO, Scope, ZIO, ZIOAppArgs, ZIOAppDefault}

import java.io.IOException

object MyConsoleApp extends ZIOAppDefault {

  lazy val myConsoleProgram: IO[IOException, Unit] =
    for {
      _ <- Console.printLine("What's your name?")
      name <- Console.readLine
      _ <- Console.printLine(s"Hi $name! Welcome to ZIO!")
    } yield ()

  lazy val myConsoleProgram2: ZIO[MyConsole, IOException, Unit] =
    for {
      console <- ZIO.service[MyConsole]
      _ <- console.printLine("What's your name?")
      name <- console.readLine
      _ <- console.printLine(s"Hi $name! Welcome to ZIO!")
    } yield ()

  override def run: ZIO[ZIOAppArgs with Scope, IOException, Unit] = myConsoleProgram

}
