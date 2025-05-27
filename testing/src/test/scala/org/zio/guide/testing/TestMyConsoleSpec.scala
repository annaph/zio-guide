package org.zio.guide.testing

import org.zio.guide.testing.utils.TestMyConsole
import zio.Scope
import zio.test.Assertion.equalTo
import zio.test.{Spec, TestEnvironment, ZIOSpecDefault, assert}

object TestMyConsoleSpec extends ZIOSpecDefault {

  override def spec: Spec[TestEnvironment with Scope, Any] = suite("TestMyConsoleSpec")(
    test("testing my console service")(
      for {
        _ <- TestMyConsole.feedLine("Anna")
        _ <- MyConsoleApp.myConsoleProgram2
        output <- TestMyConsole.output
      } yield assert(output)(
        equalTo(
          Seq(
            "What's your name?",
            "Hi Anna! Welcome to ZIO!"
          )
        )
      )
    )
  ).provide(TestMyConsole.test)

}
