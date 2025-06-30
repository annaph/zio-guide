package org.zio.guide.testing.property

import zio.Scope
import zio.test.Assertion.equalTo
import zio.test.{Gen, Spec, TestEnvironment, ZIOSpecDefault, assert, check}

object IntegerAdditionSpec extends ZIOSpecDefault {

  override def spec: Spec[TestEnvironment with Scope, Any] = suite("IntegerAdditionSpec")(
    test("integer addition is associative") {
      check(Gen.int, Gen.int, Gen.int) { (x, y, z) =>
        val left = (x + y) + z
        val right = x + (y + z)
        assert(left)(equalTo(right))
      }
    }
  )

}
