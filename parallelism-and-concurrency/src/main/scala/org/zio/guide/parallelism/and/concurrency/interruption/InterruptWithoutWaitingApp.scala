package org.zio.guide.parallelism.and.concurrency.interruption

import org.zio.guide.parallelism.and.concurrency.Implicits.{IntOps, StringOps}
import zio.{Promise, Scope, UIO, ZIO, ZIOAppArgs, ZIOAppDefault}

object InterruptWithoutWaitingApp extends ZIOAppDefault {

  private lazy val effect: UIO[Unit] =
    for {
      promise <- Promise.make[Nothing, Unit]
      childEffect = promise.succeed(()) *> ZIO.never
      childFinalizer = ZIO
        .succeed(println("Child effect finalizer".withGreenBackground))
        .delay(5.seconds)
      childFiber <- childEffect.ensuring(childFinalizer).fork
      _ <- promise.await
      _ <- childFiber.interrupt.fork
      _ <- ZIO.succeed(println("Done interrupting".withGreenBackground))
    } yield ()

  override def run: ZIO[Any with ZIOAppArgs with Scope, Nothing, Unit] = effect

}
