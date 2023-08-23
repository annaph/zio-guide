package org.zio.guide.parallelism.and.concurrency.interruption

import org.zio.guide.parallelism.and.concurrency.Implicits.{IntOps, StringOps}
import zio.{Console, Scope, UIO, ZIO, ZIOAppArgs, ZIOAppDefault}

object UnInterruptibleMaskApp extends ZIOAppDefault {

  private lazy val childEffect: UIO[Unit] = ZIO
    .sleep(12.seconds)
    .flatMap(_ => Console.printLine(line = "I'm child effect :(".withRedBackground))
    .orDie

  private lazy val parent: UIO[Unit] =
    for {
      _ <- beforeAndAfterUnInterruptible(childEffect).fork
    } yield ()

  override def run: ZIO[Any with ZIOAppArgs with Scope, Nothing, Unit] =
    for {
      parentFiber <- parent.fork
      _ <- ZIO.sleep(1.seconds)
      _ <- parentFiber.interrupt
      _ <- Console.printLine(line = s"Parent interrupted!".withMagentaBackground).orDie
    } yield ()

  private def beforeAndAfterUnInterruptible[R, E, A](effect: ZIO[R, E, A]): ZIO[R, E, A] =
    ZIO.uninterruptibleMask { restorer =>
      val before = {
        ZIO.sleep(7.seconds) *>
          ZIO.succeed(println("Some (before) work that shouldn't be interrupted".withGreenBackground)) *>
          restorer(effect)
      }.exit

      val after = before <* ZIO.succeed(println("Some (after) work that shouldn't be interrupted".withGreenBackground))

      after.unexit
    }

}
