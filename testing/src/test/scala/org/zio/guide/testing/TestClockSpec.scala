package org.zio.guide.testing

import org.zio.guide.testing.Implicits.toZIODuration
import zio.stream.ZStream
import zio.test.Assertion.{equalTo, isTrue}
import zio.test.{Spec, TestClock, TestEnvironment, ZIOSpecDefault, assert, assertCompletes}
import zio.{Promise, Queue, Ref, Schedule, Scope, ZIO}

import scala.concurrent.duration._

object TestClockSpec extends ZIOSpecDefault {

  override def spec: Spec[TestEnvironment with Scope, Any] = suite("TestClockSpec")(
    test("we can test effects involving time") {
      for {
        ref <- Ref.make(false)
        _ <- ref.set(true).delay(1.hour).fork
        _ <- TestClock.adjust(1.hour)
        value <- ref.get
      } yield assert(value)(isTrue)
    },
    test("testing a schedule") {
      for {
        ref <- Ref.make(3)
        latch <- Promise.make[Nothing, Unit]
        _ <- ref
          .updateAndGet(_ - 1)
          .flatMap { n => latch.succeed(()).when(n == 0) }
          .repeat(Schedule.fixed(2.seconds))
          .delay(1.second)
          .fork
        _ <- TestClock.adjust(5.seconds)
        _ <- latch.await
      } yield assertCompletes
    },
    test("testing a stream") {
      val s1 = ZStream.fromSchedule(Schedule.fixed(100.seconds)).map(_.toInt)
      val s2 = ZStream.fromSchedule(Schedule.fixed(70.seconds)).map(_.toInt)
      val s3 = s1.zipLatestWith(s2)((_, _))

      for {
        queue <- Queue.unbounded[(Int, Int)]
        ref <- Ref.make[Int](4)
        _ <- s3.foreach(queue.offer).fork
        readElements <- ZIO.collectAll {
          ZIO.replicate(4) {
            ref.getAndUpdate(_ - 1).flatMap(n => queue.take.when(n != 0))
          }
        }.fork
        _ <- TestClock.adjust(220.seconds)
        elements <- readElements.join
        actual = elements.filter(_.nonEmpty).map(_.get)
      } yield assert(actual)(
        equalTo(Seq((0, 0), (0, 1), (1, 1), (1, 2))
        )
      )
    }
  )

}
