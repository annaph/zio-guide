package org.zio.guide.stm.composing.atomicity

import org.zio.guide.stm.composing.atomicity.Implicits.{StringOps, threeSeconds}
import zio.stm.{STM, TRef}
import zio.{Scope, ZIO, ZIOAppArgs, ZIOAppDefault}

object RetryAutoDebitApp extends ZIOAppDefault {

  override def run: ZIO[ZIOAppArgs with Scope, Nothing, Unit] =
    for {
      account <- TRef.make(0).commit
      debit <- autoDebit(account).commit.fork
      _ <- ZIO sleep threeSeconds
      _ <- account.update(_ + 100).commit
      _ <- debit.await
    } yield ()

  private def autoDebit(account: TRef[Int], amount: Int = 100): STM[Nothing, Unit] =
    account.get.flatMap {
      case balance if balance >= amount =>
        println(s"Sufficient funds.".withGreenBackground)
        account.update(_ - amount)
      case _ =>
        println("Insufficient funds!".withRedBackground)
        STM.retry
    }

}
