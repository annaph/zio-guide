package org.zio.guide.parallelism.and.concurrency.interruption

import org.zio.guide.parallelism.and.concurrency.Implicits.StringOps
import zio.{Console, Promise, Ref, Scope, UIO, ZIO, ZIOAppArgs, ZIOAppDefault}

object InterruptChildFiberApp extends ZIOAppDefault {

  private lazy val parent: UIO[Boolean] =
    for {
      ref <- Ref.make(false)
      promise <- Promise.make[Nothing, Unit]
      childEffect = promise.succeed(()) *> ZIO.never
      finalizer = ref.set(true)
      childFiber <- childEffect.ensuring(finalizer).fork
      _ <- promise.await
      _ <- childFiber.interrupt
      value <- ref.get
    } yield value

  override def run: ZIO[Any with ZIOAppArgs with Scope, Nothing, Unit] =
    for {
      parentValue <- parent
      _ <- Console.printLine(line = s"Parent value: $parentValue!".withGreenBackground).orDie
    } yield ()

}
