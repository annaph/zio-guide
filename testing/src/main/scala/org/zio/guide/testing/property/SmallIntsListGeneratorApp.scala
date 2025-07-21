package org.zio.guide.testing.property

import org.zio.guide.testing.debug
import zio.test.Gen
import zio.{Scope, ZIO, ZIOAppArgs, ZIOAppDefault}

object SmallIntsListGeneratorApp extends ZIOAppDefault {

  override def run: ZIO[ZIOAppArgs with Scope, Nothing, Unit] =
    debug {
      genSmallIntsList(Gen.int)
    }

  private def genSmallIntsList[R, A](gen: Gen[R, A]): Gen[R, List[A]] =
    Gen
      .int(min = 1, max = 10)
      .flatMap { n => Gen.listOfN(n)(gen) }

}
