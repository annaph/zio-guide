package org.zio.guide.testing.property

import org.zio.guide.testing.Implicits.StringOps
import zio.test.Gen
import zio.{Console, Scope, ZIO, ZIOAppArgs, ZIOAppDefault}

object SmallIntsListGeneratorApp extends ZIOAppDefault {

  override def run: ZIO[ZIOAppArgs with Scope, Nothing, Unit] =
    for {
      sample <- genSmallIntsList(Gen.int).runCollectN(n = 7)
      _ <- Console.printLine(line = s"Generated sample\n${sample mkString "\n"}".withGreenBackground).orDie
    } yield ()

  private def genSmallIntsList[R, A](gen: Gen[R, A]): Gen[R, List[A]] =
    Gen
      .int(min = 1, max = 10)
      .flatMap { n => Gen.listOfN(n)(gen) }

}
