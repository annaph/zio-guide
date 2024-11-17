package org.zio.guide.advanced.error.management

import org.zio.guide.advanced.error.management.Implicits.{StringOps, oneSecond}
import zio.{Console, Duration, Schedule, Scope, UIO, ZIO, ZIOAppArgs, ZIOAppDefault}

object RepeatNTimesWithDelayApp extends ZIOAppDefault {

  private lazy val effect: UIO[Unit] =
    Console.printLine(line = s"Hi Anna!".withGreenBackground).orDie

  override def run: ZIO[ZIOAppArgs with Scope, Nothing, Unit] =
    for {
      schedulerResult <- effect.repeat(delayN(n = 7)(oneSecond))
      _ <- Console.printLine(line = s"Result from scheduler: $schedulerResult".withBlueBackground).orDie
    } yield ()

  private def delayN(n: Int)(delay: Duration): Schedule[Any, Any, Long] =
    Schedule.recurs(n) *> Schedule.spaced(delay)

}
