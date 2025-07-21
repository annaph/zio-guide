package org.zio.guide.testing.property

import org.zio.guide.testing.debug
import zio.test.Gen
import zio.{Scope, ZIO, ZIOAppArgs, ZIOAppDefault}

object EvenIntGeneratorApp extends ZIOAppDefault {

  private lazy val genEvenInt: Gen[Any, Int] =
    Gen
      .int(min = 1, max = 100)
      .map { number =>
        if (number % 2 == 0) number
        else number + 1
      }

  override def run: ZIO[ZIOAppArgs with Scope, Nothing, Unit] =
    debug {
      genEvenInt
    }

}

