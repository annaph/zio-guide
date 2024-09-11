package org.zio.guide.stm.data.structures

import org.zio.guide.stm.Implicits.StringOps
import zio.stm.TReentrantLock
import zio.{Console, Scope, UIO, ZIO, ZIOAppArgs, ZIOAppDefault}

object TReentrantLockBankAccountApp extends ZIOAppDefault {

  private var _bankAccount = 0

  override def run: ZIO[ZIOAppArgs with Scope, Nothing, Unit] =
    for {
      lock <- TReentrantLock.make.commit
      transfer1 = transfer(lock, amount = 100)
      transfer2 = transfer(lock, amount = 50)
      transfer3 = transfer(lock, amount = 25)
      _ <- ZIO.collectAllParDiscard(as = Seq(transfer1, transfer2, transfer3))
      _ <- Console.printLine(line = s"Bank account balance: '${_bankAccount}'".withGreenBackground).orDie
    } yield ()

  private def transfer(lock: TReentrantLock, amount: Int): UIO[Unit] =
    ZIO.scoped {
      lock.writeLock *> ZIO.succeed(_bankAccount += amount)
    }

}
