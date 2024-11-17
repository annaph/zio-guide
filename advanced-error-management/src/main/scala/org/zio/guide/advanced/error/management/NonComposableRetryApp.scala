package org.zio.guide.advanced.error.management

import org.zio.guide.advanced.error.management.Implicits.{StringOps, threeSeconds}
import zio.{Console, Duration, Ref, Scope, Task, ZIO, ZIOAppArgs, ZIOAppDefault}

object NonComposableRetryApp extends ZIOAppDefault {

  override def run: ZIO[ZIOAppArgs with Scope, Nothing, Unit] =
    for {
      refA <- Ref.make(0)
      refB <- Ref.make(0)
      effectA = effect(name = "A", counter = refA, threshold = 2)
      effectB = effect(name = "B", counter = refB, threshold = 12)
      _ <- retryWithDelayN(n = 3)(effectA)(threeSeconds).catchAll(_ => ZIO.unit)
      _ <- retryWithDelayN(n = 3)(effectB)(threeSeconds).catchAll(_ => ZIO.unit)
    } yield ()

  private def retryWithDelayN[R, E, A](n: Int)(effect: ZIO[R, E, A])(duration: Duration): ZIO[R, E, A] =
    if (n <= 1) effect
    else effect.catchAll(_ => retryWithDelayN(n - 1)(effect)(duration).delay(duration))

  private def effect(name: String, counter: Ref[Int], threshold: Int): Task[Unit] =
    counter.get.flatMap { n =>
      if (n == threshold) Console.printLine(line = s"Effect $name succeeded".withGreenBackground).orDie
      else counter.set(n + 1) *> ZIO
        .fail(new Exception(s"Effect $name failed!"))
        .tapError { e => Console.printLine(line = s"${e.getMessage}".withRedBackground).orDie }
    }

}
