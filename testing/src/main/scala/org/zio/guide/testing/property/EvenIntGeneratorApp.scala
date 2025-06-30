package org.zio.guide.testing.property

import org.zio.guide.testing.Implicits.StringOps
import zio.test.Gen
import zio.{Console, Scope, ZIO, ZIOAppArgs, ZIOAppDefault}

object EvenIntGeneratorApp extends ZIOAppDefault {

  private lazy val genEvenInt: Gen[Any, Int] =
    Gen
      .int(min = 1, max = 100)
      .map { number =>
        if (number % 2 == 0) number
        else number + 1
      }

  override def run: ZIO[ZIOAppArgs with Scope, Nothing, Unit] =
    for {
      sample <- genEvenInt.runCollectN(n = 7)
      _ <- Console.printLine(s"Generated sample\n${sample mkString "\n"}".withGreenBackground).orDie
    } yield ()

}

