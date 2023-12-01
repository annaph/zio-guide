package org.zio.guide.concurrent.structures.queue

import org.zio.guide.concurrent.structures.Implicits.{StringOps, threeSeconds}
import zio.{Clock, Console, Queue, Scope, UIO, ZIO, ZIOAppArgs, ZIOAppDefault}

object TakeForeverApp extends ZIOAppDefault {

  private lazy val myQueueWork: UIO[Unit] = {
    for {
      queue <- Queue.unbounded[Int]
      _ <- queue.take
        .tap(n => Console.printLine(line = s"Got $n!".withGreenBackground))
        .forever
        .fork
      _ <- queue offer 1
      _ <- queue offer 2
    } yield ()
  }

  override def run: ZIO[Any with ZIOAppArgs with Scope, Nothing, Unit] =
    for {
      _ <- myQueueWork
      _ <- Clock sleep threeSeconds
    } yield ()

}
