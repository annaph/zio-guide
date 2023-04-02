package org.zio.guide.essentials.testing

import org.zio.guide.essentials.testing.GoShoppingApp.goShopping
import org.zio.guide.essentials.testing.Implicits.IntOps
import zio.Scope
import zio.test.TestAspect.timeout
import zio.test.{Spec, TestClock, TestEnvironment, ZIOSpecDefault, assertCompletes}

object GoShoppingSpec extends ZIOSpecDefault {

  override def spec: Spec[TestEnvironment with Scope, Any] = suite(label = "GoShoppingSpec")(
    test(label = "goShoppingDelays for 7 seconds") {
      for {
        fiber <- goShopping.fork
        _ <- TestClock.adjust(7.seconds)
        _ <- fiber.join
      } yield assertCompletes
    } @@ timeout(3.seconds)
  )

}
