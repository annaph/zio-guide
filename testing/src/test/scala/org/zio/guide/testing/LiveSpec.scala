package org.zio.guide.testing

import org.zio.guide.testing.Implicits.toZIODuration
import zio.test.Assertion.{equalTo, isNone, isSome}
import zio.test.{Live, Spec, TestEnvironment, ZIOSpecDefault, assert}
import zio.{Scope, System, ZIO}

import scala.concurrent.duration._

object LiveSpec extends ZIOSpecDefault {

  override def spec: Spec[TestEnvironment with Scope, Any] = suite("LiveSpec")(
    test("testing with actual env variable") {
      for {
        myVariable <- Live.live(System.env("HOME"))
      } yield assert(myVariable)(isSome) &&
        assert(myVariable.get.take(5))(equalTo("/home"))
    },
    test("testing delay with actual Clock service") {
      val effect = ZIO.attempt {
        Thread sleep 7000
        0
      }

      for {
        result <- Live.withLive(effect)(_.timeout(3.seconds))
      } yield assert(result)(isNone)
    }
  )

}
