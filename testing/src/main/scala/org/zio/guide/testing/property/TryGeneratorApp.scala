package org.zio.guide.testing.property

import org.zio.guide.testing.debug
import zio.test.Gen
import zio.{Scope, ZIO, ZIOAppArgs, ZIOAppDefault}

import scala.util.{Failure, Success, Try}

object TryGeneratorApp extends ZIOAppDefault {

  override def run: ZIO[ZIOAppArgs with Scope, Nothing, Unit] =
    debug {
      genTry(Gen.int(min = 1, max = 12))
    }

  private def genTry[R, A](gen: Gen[R, A]): Gen[R, Try[A]] =
    Gen
      .either(left = Gen.throwable, right = gen)
      .map {
        case Left(e) => Failure(e)
        case Right(a) => Success(a)
      }

}
