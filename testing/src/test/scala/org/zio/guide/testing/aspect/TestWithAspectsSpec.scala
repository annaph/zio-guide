package org.zio.guide.testing.aspect

import org.zio.guide.testing.Implicits.toZIODuration
import zio.test.Assertion.equalTo
import zio.test.TestAspect.{jvmOnly, nonFlaky, timeout}
import zio.test.{Spec, TestEnvironment, ZIOSpecDefault, assert}
import zio.{Scope, ZIO}

import scala.concurrent.duration._

object TestWithAspectsSpec extends ZIOSpecDefault {

  override def spec: Spec[TestEnvironment with Scope, Any] = suite("TestWithAspectsSpec")(
    test("foreachPar preserves ordering") {
      for {
        values <- ZIO.foreachPar(1 to 100)(ZIO.succeed(_))
      } yield assert(values)(equalTo(1 to 100))
    }
  ) @@ nonFlaky(100) @@ jvmOnly @@ timeout(60.seconds)

}
