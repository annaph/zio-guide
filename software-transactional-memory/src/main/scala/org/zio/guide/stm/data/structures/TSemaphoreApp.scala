package org.zio.guide.stm.data.structures

import org.zio.guide.stm.Implicits.StringOps
import zio.stm.{STM, TRef, USTM}
import zio.{Console, Scope, UIO, ZIO, ZIOAppArgs, ZIOAppDefault}

object TSemaphoreApp extends ZIOAppDefault {

  private var _bankAccount = 0

  override def run: ZIO[ZIOAppArgs with Scope, Nothing, Unit] =
    for {
      semaphore <- MyTSemaphore.make().commit
      transfer1 = transfer(semaphore, amount = 100)
      transfer2 = transfer(semaphore, amount = 50)
      transfer3 = transfer(semaphore, amount = 25)
      _ <- ZIO.collectAllParDiscard(as = Seq(transfer1, transfer2, transfer3))
      _ <- Console.printLine(line = s"Bank account balance: '${_bankAccount}'".withGreenBackground).orDie
    } yield ()

  private def transfer(semaphore: MyTSemaphore, amount: Int): UIO[Unit] =
    ZIO.scoped {
      for {
        _ <- ZIO.acquireRelease(acquire = semaphore.acquire.commit)(release = _ => semaphore.release.commit)
        _ <- ZIO.succeed(_bankAccount += amount)
      } yield ()
    }

  private final class MyTSemaphore private(private val permits: TRef[Long]) {

    def acquire: USTM[Unit] =
      permits.get.flatMap { n =>
        if (n == 0) STM.retry
        else permits.update(_ - 1)
      }

    def release: USTM[Unit] =
      permits.update(_ + 1)

  }

  private object MyTSemaphore {
    def make(permits: Long = 1): USTM[MyTSemaphore] =
      TRef
        .make(permits)
        .map(new MyTSemaphore(_))
  }

}
