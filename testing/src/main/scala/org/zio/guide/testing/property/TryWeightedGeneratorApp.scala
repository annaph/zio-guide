package org.zio.guide.testing.property

import org.zio.guide.testing.debug
import zio.test.Gen
import zio.{Scope, ZIO, ZIOAppArgs, ZIOAppDefault}

import scala.util.{Failure, Success, Try}

object TryWeightedGeneratorApp extends ZIOAppDefault {

  override def run: ZIO[ZIOAppArgs with Scope, Nothing, Unit] =
    debug {
      genTryWeighted(Gen.int(min = 1, max = 12))
    }

  private def genTryWeighted[R, A](gen: Gen[R, A]): Gen[R, Try[A]] =
    Gen
      .int(min = 1, max = 100)
      .flatMap { weight =>
        if (weight > 10) gen.map(Success(_))
        else Gen.throwable.map(Failure(_))
      }

}
