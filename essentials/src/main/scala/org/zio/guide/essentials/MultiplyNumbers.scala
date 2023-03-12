package org.zio.guide.essentials

import zio.{Console, Scope, Task, UIO, ZIO, ZIOAppArgs, ZIOAppDefault}

import java.io.IOException

object MultiplyNumbers extends ZIOAppDefault {

  private val multiplyNumbers: UIO[Int] =
    for {
      x <- readNumberOrRetry(label = "x")
      y <- readNumberOrRetry(label = "y")
    } yield x * y

  override def run: ZIO[Any with ZIOAppArgs with Scope, IOException, Unit] =
    multiplyNumbers.flatMap { result =>
      Console.printLine(line = s"Result: $result")
    }

  private def readNumberOrRetry(label: String): UIO[Int] =
    readNumber(label)
      .orElse {
        Console.printLine(line = "Please enter a valid integer").orElse(ZIO.succeed(())) *>
          readNumberOrRetry(label)
      }

  private def readNumber(label: String): Task[Int] =
    for {
      _ <- Console.printLine(line = s"$label:")
      str <- Console.readLine
      number <- ZIO.attempt(str.toInt)
    } yield number

}
