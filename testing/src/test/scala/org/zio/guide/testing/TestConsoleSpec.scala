package org.zio.guide.testing

import zio.test.Assertion.equalTo
import zio.test.{Spec, TestAspect, TestConsole, TestEnvironment, ZIOSpecDefault, assert, assertCompletes}
import zio.{Console, Scope}

object TestConsoleSpec extends ZIOSpecDefault {

  override def spec: Spec[TestEnvironment with Scope, Any] = suite("TestConsoleSpec")(
    test("testing a console program deterministically") {
      for {
        _ <- TestConsole.feedLines("Anna")
        _ <- MyConsoleApp.myConsoleProgram
        output <- TestConsole.output
      } yield assert(output)(
        equalTo(
          Seq(
            "What's your name?\n",
            "Hi Anna! Welcome to ZIO!\n"
          )
        )
      )
    } @@ TestAspect.silent,
    test("testing silent/debug mode") {
      for {
        _ <- TestConsole.debug {
          Console.printLine("Should be printed")
        }
        _ <- TestConsole.silent {
          Console.printLine("Should not be printed!")
        }
      } yield assertCompletes
    }
  )

}
