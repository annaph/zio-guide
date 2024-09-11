package org.zio.guide.stm.composing.atomicity

import org.zio.guide.stm.Implicits.StringOps
import zio.stm.TRef
import zio.{Console, Scope, UIO, ZIO, ZIOAppArgs, ZIOAppDefault}

object IncrementTRefApp extends ZIOAppDefault {

  private lazy val createAndUpdateTRef: UIO[Int] =
    for {
      ref <- TRef.make(0).commit
      increment = ref.get.flatMap(n => ref.set(n + 1)).commit
      _ <- ZIO.collectAllPar(ZIO.replicate(10000)(increment))
      value <- ref.get.commit
    } yield value

  override def run: ZIO[ZIOAppArgs with Scope, Nothing, Unit] =
    for {
      result <- createAndUpdateTRef
      _ <- Console.printLine(line = s"result: $result".withGreenBackground).orDie
    } yield ()

}
