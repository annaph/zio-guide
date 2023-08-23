package org.zio.guide.parallelism.and.concurrency.interruption

import org.zio.guide.parallelism.and.concurrency.Implicits.{IntOps, StringOps}
import zio.{Console, Scope, Task, UIO, ZIO, ZIOAppArgs, ZIOAppDefault}

import java.util.concurrent.atomic.AtomicBoolean
import scala.annotation.tailrec

object InterruptSideEffectingCode extends ZIOAppDefault {

  private lazy val parent: UIO[Unit] =
    for {
      ref <- ZIO.succeed(new AtomicBoolean(false))
      childEffect = effect(sideEffectingBlock, cancel, ref)
      childFiber <- childEffect.fork
      - <- ZIO.sleep(1.seconds)
      _ <- childFiber.interrupt
    } yield ()

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Unit] =
    for {
      _ <- parent
      _ <- Console.printLine(line = s"Side effecting code interrupted!".withGreenBackground).orDie
    } yield ()

  private def effect(block: AtomicBoolean => Unit, cancel: AtomicBoolean => UIO[Unit], ref: AtomicBoolean): Task[Unit] =
    ZIO.attemptBlockingCancelable(block(ref))(cancel(ref))

  private def cancel(ref: AtomicBoolean): UIO[Unit] =
    ZIO.succeed(ref.set(true))

  private def sideEffectingBlock(ref: AtomicBoolean): Unit = {
    @tailrec
    def go(i: Int): Unit =
      if (i == 1000000 || ref.get()) ()
      else {
        println(i.toString.withMagentaBackground)
        go(i + 1)
      }

    go(0)
  }

}
