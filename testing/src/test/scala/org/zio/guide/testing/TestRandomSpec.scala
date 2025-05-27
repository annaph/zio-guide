package org.zio.guide.testing

import zio.test.Assertion.equalTo
import zio.test.{Spec, TestEnvironment, TestRandom, ZIOSpecDefault, assert}
import zio.{Random, Scope}

object TestRandomSpec extends ZIOSpecDefault {

  override def spec: Spec[TestEnvironment with Scope, Any] = suite("TestRandomSpec")(
    test("testing with specific seed") {
      for {
        _ <- TestRandom.setSeed(42L)
        first <- Random.nextLong
        _ <- TestRandom.setSeed(42L)
        second <- Random.nextLong
      } yield assert(first)(equalTo(second))
    },
    test("testing with fed data") {
      for {
        _ <- TestRandom.feedInts(1, 2, 3)
        x <- Random.nextInt
        y <- Random.nextInt
        z <- Random.nextInt
      } yield assert((x, y, z))(
        equalTo((1, 2, 3))
      )
    }
  )

}
