package org.zio.guide.concurrent.structures.semaphore

import org.zio.guide.concurrent.structures.Implicits.StringOps
import zio.{Console, Scope, UIO, ZIO, ZIOAppArgs, ZIOAppDefault}

object RefMApp extends ZIOAppDefault {

  override def run: ZIO[ZIOAppArgs with Scope, Nothing, Unit] =
    for {
      ref <- RefM.make[Int](0)
      child = ZIO.foreachPar(Seq(1, 2, 3))(_ => increment(ref))
      left <- child.fork
      right <- child.fork
      _ <- left.join
      _ <- right.join
      result <- ref.get
      _ <- Console.printLine(line = s"Reference value: '$result'".withGreenBackground).orDie
    } yield ()

  private def increment(ref: RefM[Int]): UIO[Unit] =
    ref.modify { i =>
      ZIO.succeed(i + 1)
        .tap(j => Console.printLine(line = s"Incrementing ~~> '$i' -> '$j'".withBlueBackground).orDie)
        .map(j => () -> j)
    }

}
