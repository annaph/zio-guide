package org.zio.guide.essentials.exercises

import org.zio.guide.essentials.exercises.Implicits.StringOps
import zio.{Console, Random, Scope, ZIO, ZIOAppArgs, ZIOAppDefault}

import java.io.IOException

object ConsoleChatEx {

  def readUntil(acceptInput: String => Boolean): ZIO[Any, IOException, String] =
    Console.readLine(prompt = "Enter: ").flatMap { input =>
      if (acceptInput(input)) ZIO.succeed(input)
      else Console
        .printLine(line = "Input not acceptable".withRedBackground)
        .flatMap(_ => readUntil(acceptInput))
    }

  def doWhile[R, E, A](body: ZIO[R, E, A])(condition: A => Boolean): ZIO[R, E, A] =
    body.flatMap { result =>
      if (condition(result)) ZIO.succeed(result)
      else doWhile(body)(condition)
    }

}

object HelloHumanApp extends ZIOAppDefault {

  override def run: ZIO[Any with ZIOAppArgs with Scope, IOException, Unit] =
    for {
      _ <- Console.printLine(line = "What is your name")
      name <- Console.readLine
      _ <- Console.printLine(line = s"Hello, $name".withGreenBackground)
    } yield ()

}

object NumberGuessingApp extends ZIOAppDefault {

  override def run: ZIO[Any with ZIOAppArgs with Scope, IOException, Unit] =
    for {
      random <- Random.nextIntBetween(minInclusive = 1, maxExclusive = 4)
      _ <- Console.printLine(line = "Guess  number from 1 to 3:")
      number <- Console.readLine.map(_.toInt)
      _ <-
        if (number == random) Console.printLine(line = s"You guessed right!".withGreenBackground)
        else Console.printLine(line = s"You guessed wrong, the number was $random!".withRedBackground)
    } yield ()

}

object AcceptableNameApp extends ZIOAppDefault {

  import ConsoleChatEx.readUntil

  private val names = Set("Anna", "Stacey", "Nicole")

  override def run: ZIO[Any with ZIOAppArgs with Scope, IOException, Any] =
    for {
      name <- readUntil(acceptInput = names.contains)
      _ <- Console.printLine(line = s"You entered acceptable name: $name".withGreenBackground)
    } yield ()

}

object DoWhileApp extends ZIOAppDefault {

  import ConsoleChatEx.doWhile

  private lazy val readLines: ZIO[Any, IOException, String] = {
    val readLine =
      for {
        _ <- Console.printLine(line = "Enter line: ")
        line <- Console.readLine
      } yield line

    doWhile(readLine)(condition = _ == "STOP")
  }

  override def run: ZIO[Any with ZIOAppArgs with Scope, IOException, Unit] =
    readLines
      .flatMap(_ => Console.printLine(line = "Finished reading lines :)".withGreenBackground))

}
