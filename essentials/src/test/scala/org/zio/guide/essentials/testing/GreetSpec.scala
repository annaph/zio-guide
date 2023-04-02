package org.zio.guide.essentials.testing

import org.zio.guide.essentials.first.steps.exercises.Implicits.StringOps
import org.zio.guide.essentials.testing.GreetApp.greet
import zio.Scope
import zio.test.Assertion.equalTo
import zio.test.{Spec, TestConsole, TestEnvironment, ZIOSpecDefault, assert}

object GreetSpec extends ZIOSpecDefault {

  override def spec: Spec[TestEnvironment with Scope, Any] = suite(label = "GreetSpec")(
    test(label = "greet says hello to the user") {
      val expected = s"${"Hello, Anna!".withGreenBackground}\n"
      for {
        _ <- TestConsole.feedLines(lines = "Anna")
        _ <- greet
        value <- TestConsole.output
      } yield assert(value)(equalTo(Vector(expected)))
    }
  )

}
