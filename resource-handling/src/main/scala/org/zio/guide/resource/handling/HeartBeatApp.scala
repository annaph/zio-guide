package org.zio.guide.resource.handling

import org.zio.guide.resource.handling.Implicits.{StringOps, oneSecond, sevenSeconds}
import zio.{Clock, Console, Fiber, Scope, Task, ZIO, ZIOAppArgs, ZIOAppDefault}

object HeartBeatApp extends ZIOAppDefault {

  private lazy val heartBeat: ZIO[Scope, Nothing, Fiber[Throwable, Unit]] =
    Console
      .printLine(line = s"~ Heartbeat signal ~".withBlueBackground)
      .delay(oneSecond)
      .forever
      .forkScoped

  private lazy val someWork: Task[Unit] =
    ZIO.attempt {
      println(s"Starting some work...".withGreenBackground)
      Thread sleep (12 * 1000)
      println(s"Finished with some work!".withGreenBackground)
    }

  private lazy val someWorkWithHeartBeat: Task[Unit] =
    ZIO.scoped {
      for {
        _ <- heartBeat
        _ <- someWork
      } yield ()
    }

  override def run: ZIO[ZIOAppArgs with Scope, Throwable, Unit] =
    for {
      _ <- someWorkWithHeartBeat
      _ <- Clock sleep sevenSeconds
      _ <- Console.printLine(line = s"Exiting...".withGreenBackground)
    } yield ()

}
