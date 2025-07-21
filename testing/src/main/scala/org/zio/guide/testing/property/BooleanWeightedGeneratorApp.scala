package org.zio.guide.testing.property

import org.zio.guide.testing.debug
import zio.test.Gen
import zio.{Scope, ZIO, ZIOAppArgs, ZIOAppDefault}

object BooleanWeightedGeneratorApp extends ZIOAppDefault {

  private lazy val genBoolean: Gen[Any, Boolean] =
    Gen.weighted(
      Gen.const(true) -> 9,
      Gen.const(false) -> 1
    )

  override def run: ZIO[ZIOAppArgs with Scope, Nothing, Unit] =
    debug {
      genBoolean
    }

}
