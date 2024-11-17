package org.zio.guide.advanced.error.management

import org.zio.guide.advanced.error.management.Implicits.{StringOps, oneSecond}
import zio.{Console, Duration, Schedule, Scope, UIO, ZIO, ZIOAppArgs, ZIOAppDefault}

import java.time.LocalDateTime

object FibonacciScheduleApp extends ZIOAppDefault {

  private lazy val effect: UIO[Unit] =
    Console.printLine(line = s"Hi Anna! Now is: ${LocalDateTime.now}".withGreenBackground).orDie

  override def run: ZIO[ZIOAppArgs with Scope, Nothing, Unit] =
    effect
      .repeat(fibonacci(oneSecond) && Schedule.recurs(7))
      .as(())

  private def fibonacci(z: Duration): Schedule[Any, Any, Duration] =
    Schedule
      .unfold((z, z)) {
        case (curr, next) =>
          next -> (curr plus next)
      }
      .map(_._1)
      .addDelay(out => out)

}
