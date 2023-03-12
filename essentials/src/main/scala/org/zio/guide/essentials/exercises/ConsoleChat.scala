package org.zio.guide.essentials.exercises

import org.zio.guide.essentials.exercises.Implicits.StringOps
import zio.{Scope, Task, ZIO, ZIOAppArgs, ZIOAppDefault}

import scala.io.StdIn
import scala.util.Random

object ConsoleChat {

  def printLine(line: String): Task[Unit] = ZIO.attempt {
    println(line)
  }

  def readLine: Task[String] = ZIO.attempt {
    StdIn.readLine()
  }

  def random: Task[Int] = ZIO.attempt {
    Random.nextInt(3) + 1
  }

}

object NameChatApp extends ZIOAppDefault {

  override def run: ZIO[Any with ZIOAppArgs with Scope, Throwable, Unit] =
    for {
      _ <- ConsoleChat.printLine(line = "What is your name")
      name <- ConsoleChat.readLine
      _ <- ConsoleChat.printLine(line = s"Hello, $name".withGreenBackground)
    } yield ()

}

object GuessNumberApp extends ZIOAppDefault {

  override def run: ZIO[Any with ZIOAppArgs with Scope, Throwable, Unit] =
    for {
      random <- ConsoleChat.random
      _ <- ConsoleChat.printLine(line = "Guess  number from 1 to 3:")
      number <- ConsoleChat.readLine.map(_.toInt)
      _ <-
        if (number == random) ConsoleChat.printLine(line = s"You guessed right!".withGreenBackground)
        else ConsoleChat.printLine(line = s"You guessed wrong, the number was $random!".withRedBackground)
    } yield ()

}
