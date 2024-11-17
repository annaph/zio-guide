package org.zio.guide.advanced.error.management

import org.zio.guide.advanced.error.management.Implicits.{StringOps, oneSecond, threeSeconds}
import zio.{Console, Duration, IO, Ref, Schedule, Scope, ZIO, ZIOAppArgs, ZIOAppDefault}

object AlternativeScheduleApp extends ZIOAppDefault {

  private lazy val schedule1: Schedule[Any, ConnectionError.type, Duration] =
    Schedule
      .spaced(oneSecond)
      .as(oneSecond)
      .tapInput[Any, ConnectionError.type] { error =>
        Console.printLine(line = s"~>${error.getClass.getSimpleName} schedule".withRedBackground).orDie
      }

  private lazy val schedule2: Schedule[Any, RateLimitError.type, Duration] =
    Schedule
      .spaced(threeSeconds)
      .as(threeSeconds)
      .tapInput[Any, RateLimitError.type] { error =>
        Console.printLine(line = s"~>${error.getClass.getSimpleName} schedule".withRedBackground).orDie
      }

  private lazy val schedule3: Schedule[Any, WebServiceError, Duration] =
    (schedule1 ||| schedule2).contramap {
      case e: ConnectionError.type => Left(e)
      case e: RateLimitError.type => Right(e)
    }

  override def run: ZIO[ZIOAppArgs with Scope, WebServiceError, Unit] =
    for {
      counter <- Ref.make(0)
      _ <- effect(counter).retry(schedule3)
      _ <- Console.printLine(line = "Effect succeeded".withGreenBackground).orDie
    } yield ()

  private def effect(counter: Ref[Int]): IO[WebServiceError, Unit] =
    counter
      .getAndUpdate(_ + 1)
      .flatMap {
        case 0 => ZIO.fail(ConnectionError)
        case 1 => ZIO.fail(RateLimitError)
        case _ => ZIO.succeed(())
      }

  sealed trait WebServiceError

  private case object ConnectionError extends WebServiceError

  private case object RateLimitError extends WebServiceError

}
