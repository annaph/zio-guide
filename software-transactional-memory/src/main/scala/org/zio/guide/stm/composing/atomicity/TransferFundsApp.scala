package org.zio.guide.stm.composing.atomicity

import org.zio.guide.stm.composing.atomicity.Implicits.StringOps
import zio.stm.{STM, TRef}
import zio.{Console, Scope, UIO, ZIO, ZIOAppArgs, ZIOAppDefault}

object TransferFundsApp extends ZIOAppDefault {

  override def run: ZIO[ZIOAppArgs with Scope, Nothing, Unit] = {
    for {
      anna <- TRef.make(0).commit
      stacey <- TRef.make(50).commit
      nicole <- TRef.make(75).commit
      _ <- printAccounts(title = "Before transfers", accounts = "anna" -> anna, "stacey" -> stacey, "nicole" -> nicole)
      _ <- Console.printLine(line = s"Executing transfers...".withGreenBackground).orDie
      transfer1 <- transfer(from = stacey, to = anna, amount = 45).commit.fork
      transfer2 <- transfer(from = nicole, to = anna, amount = 70).commit.fork
      _ <- transfer1.await
      _ <- transfer2.await
      _ <- printAccounts(title = "After transfers", accounts = "anna" -> anna, "stacey" -> stacey, "nicole" -> nicole)
    } yield ()
  }

  private def transfer(from: TRef[Int], to: TRef[Int], amount: Int): STM[Throwable, Unit] =
    from.get.flatMap { fromBalance =>
      if (fromBalance >= amount) from.update(_ - amount) *> to.update(_ + amount)
      else STM.fail(new UnsupportedOperationException("Insufficient funds!"))
    }

  private def printAccounts(title: String, accounts: (String, TRef[Int])*): UIO[Unit] =
    Console.printLine(line = s"~> $title".withGreenBackground).orDie *> ZIO.foreachDiscard(accounts) {
      case (name, balance) => printAccount(name, balance)
    }

  private def printAccount(name: String, balance: TRef[Int]): UIO[Unit] =
    for {
      balanceValue <- balance.get.commit
      _ <- Console.printLine(line = s"$name=$balanceValue".withBlueBackground).orDie
    } yield ()

}
