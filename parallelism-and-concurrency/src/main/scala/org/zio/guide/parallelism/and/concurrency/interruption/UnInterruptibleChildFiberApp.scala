package org.zio.guide.parallelism.and.concurrency.interruption

import org.zio.guide.parallelism.and.concurrency.Implicits.{IntOps, StringOps}
import zio.{Console, Ref, Scope, UIO, ZIO, ZIOAppArgs, ZIOAppDefault}

object UnInterruptibleChildFiberApp extends ZIOAppDefault {

  private lazy val parent: UIO[Boolean] =
    for {
      ref <- Ref.make(false)
      childEffect = ref.set(true).delay(3.seconds)
      childFiber <- childEffect.fork.uninterruptible
      _ <- childFiber.interrupt
      value <- ref.get
    } yield value

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] =
    for {
      parentValue <- parent
      _ <- Console.printLine(line = s"Parent value: $parentValue!".withGreenBackground).orDie
    } yield ()

}
