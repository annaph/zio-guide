package org.zio.guide.advanced.error.management

import org.zio.guide.advanced.error.management.Implicits.{StringOps, oneSecond, sevenSeconds, threeSeconds}
import zio.Schedule.{Decision, Interval}
import zio.{Console, Duration, Ref, Schedule, Scope, Task, Trace, UIO, ZIO, ZIOAppArgs, ZIOAppDefault}

import java.time.{Instant, OffsetDateTime}

object CustomScheduleApp extends ZIOAppDefault {

  private lazy val effect: Task[Unit] =
    Console.printLine(line = s"Hello Anna! Now is: ${Instant.now}".withGreenBackground) *>
      ZIO.fail(new Exception("Failing!"))

  override def run: ZIO[ZIOAppArgs with Scope, Unit, Unit] =
    for {
      ref <- Ref.make(oneSecond)
      changeDelayFiber <- changeDelay(ref).fork
      _ <- effect
        .retry(CustomSchedule(maxRetries = 12, delayRef = ref))
        .catchAll(_ => Console.printLine(line = s"Could not recover!".withRedBackground)).orDie
      _ <- changeDelayFiber.join
    } yield ()

  private def changeDelay(ref: Ref[Duration]): UIO[Unit] =
    for {
      _ <- ZIO sleep sevenSeconds
      _ <- Console.printLine(line = s"Changing delay to 3sec".withBlueBackground).orDie
      _ <- ref.set(threeSeconds)
    } yield ()

  class CustomSchedule(maxRetries: Int, delayRef: Ref[Duration]) extends Schedule[Any, Any, Long] {
    type State = Long

    override def initial: Long = 1

    override def step(now: OffsetDateTime,
                      in: Any,
                      state: Long)(implicit trace: Trace): ZIO[Any, Nothing, (Long, Long, Decision)] =
      if (maxRetries == 0) ZIO.succeed((state, state, Decision.Done))
      else if (maxRetries > 0 && state == (maxRetries + 1)) ZIO.succeed((state, state, Decision.Done))
      else
        for {
          _ <- Console.printLine(line = s"To retry, attempt $state".withBlueBackground).orDie
          delay <- delayRef.get
        } yield {
          val newState = state + 1
          val decision = Decision.Continue(interval = Interval.after(now plusSeconds delay.toSeconds))

          (newState, newState, decision)
        }
  }

  private object CustomSchedule {
    def apply(maxRetries: Int, delayRef: Ref[Duration]): CustomSchedule =
      new CustomSchedule(maxRetries, delayRef)
  }

}
