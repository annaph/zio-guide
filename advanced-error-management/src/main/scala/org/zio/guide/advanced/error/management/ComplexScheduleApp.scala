package org.zio.guide.advanced.error.management

import org.zio.guide.advanced.error.management.Implicits.{StringOps, oneSecond}
import zio.{Console, Duration, Schedule, Scope, UIO, ZIO, ZIOAppArgs, ZIOAppDefault}

import java.time.Instant

object ComplexScheduleApp extends ZIOAppDefault {

  private lazy val effect: UIO[Unit] =
    Console.printLine(line = s"Hi Anna! Now is: ${Instant.now}".withGreenBackground).orDie

  private lazy val schedule: Schedule[Any, Any, Duration] =
    (Schedule.recurs(2) *> Schedule.spaced(oneSecond)).as(oneSecond) ++
      (Schedule.recurs(4) *> Schedule.exponential(base = oneSecond))

  override def run: ZIO[ZIOAppArgs with Scope, Nothing, Unit] =
    effect.repeat(schedule).as(())

}
