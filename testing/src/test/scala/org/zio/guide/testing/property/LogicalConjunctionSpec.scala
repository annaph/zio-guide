package org.zio.guide.testing.property

import zio.Scope
import zio.test.Assertion.equalTo
import zio.test.{Gen, Spec, TestEnvironment, ZIOSpecDefault, assert, checkAll}

object LogicalConjunctionSpec extends ZIOSpecDefault {

  private lazy val genBoolean: Gen[Any, Boolean] = Gen.fromIterable(List(true, false))

  override def spec: Spec[TestEnvironment with Scope, Any] = suite("LogicalConjunctionSpec")(
    test("logical conjunction is associative") {
      checkAll(genBoolean, genBoolean, genBoolean) { (x, y, z) =>
        val left = (x && y) && z
        val right = x && (y && z)

        assert(left)(equalTo(right))
      }
    }
  )

}
