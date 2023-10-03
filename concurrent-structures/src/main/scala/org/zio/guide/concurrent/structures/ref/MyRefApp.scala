package org.zio.guide.concurrent.structures.ref

import org.zio.guide.concurrent.structures.Implicits.StringOps
import zio.{Console, Scope, UIO, ZIO, ZIOAppArgs, ZIOAppDefault}

object MyRefApp extends ZIOAppDefault {

  private lazy val createAndUpdateMyRef: UIO[Int] =
    for {
      myRef <- MyRef.make(0)
      seq = 1 to 10000
      _ <- ZIO.foreachParDiscard(seq) { _ => myRef.update(_ + 1) }
      result <- myRef.get
    } yield result

  override def run: ZIO[Any with ZIOAppArgs with Scope, Nothing, Unit] =
    for {
      refState <- createAndUpdateMyRef
      _ <- Console.printLine(line = s"reference state: $refState".withGreenBackground).orDie
    } yield ()

}
