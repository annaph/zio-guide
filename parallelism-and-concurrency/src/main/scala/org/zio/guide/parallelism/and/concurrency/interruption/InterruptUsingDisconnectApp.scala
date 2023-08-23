package org.zio.guide.parallelism.and.concurrency.interruption

import org.zio.guide.parallelism.and.concurrency.Implicits.{IntOps, StringOps}
import zio.{Clock, Scope, UIO, ZIO, ZIOAppArgs, ZIOAppDefault}

object InterruptUsingDisconnectApp extends ZIOAppDefault {

  private lazy val a: UIO[Unit] = ZIO
    .never
    .delay(2.seconds)
    .ensuring {
      ZIO.succeed(println("Closed A".withGreenBackground)).delay(3.seconds)
    }

  private lazy val b: UIO[Unit] = ZIO
    .never
    .ensuring {
      ZIO.succeed(println("Closed B".withGreenBackground)).delay(7.seconds)
    }.disconnect

  override def run: ZIO[Any with ZIOAppArgs with Scope, Nothing, Unit] =
    for {
      aFiber <- a.fork
      bFiber <- b.fork
      _ <- Clock.sleep(1.seconds)
      _ <- aFiber.interrupt
      _ <- bFiber.interrupt
      _ <- ZIO.succeed(println("Done interrupting".withGreenBackground))
      _ <- Clock.sleep(12.seconds)
    } yield ()

}
