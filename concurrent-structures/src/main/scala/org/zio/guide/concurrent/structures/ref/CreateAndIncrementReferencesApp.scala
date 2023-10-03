package org.zio.guide.concurrent.structures.ref

import org.zio.guide.concurrent.structures.Implicits.StringOps
import zio.{Console, Ref, Scope, UIO, ZIO, ZIOAppArgs, ZIOAppDefault}

object CreateAndIncrementReferencesApp extends ZIOAppDefault {

  private lazy val makeRef1: UIO[Ref[Int]] = makeRef

  private lazy val makeRef2: UIO[Ref[Int]] = makeRef

  private lazy val createAndUpdateReferences: UIO[(Int, Int)] =
    for {
      ref1 <- makeRef1
      ref2 <- makeRef2
      _ <- ref1.update(_ + 1)
      _ <- ref2.update(_ + 1)
      l <- ref1.get
      r <- ref2.get
    } yield (l, r)

  override def run: ZIO[Any with ZIOAppArgs with Scope, Nothing, Unit] =
    for {
      result <- createAndUpdateReferences
      (l, r) = result
      _ <- Console.printLine(line = s"left: $l, right: $r".withGreenBackground).orDie
    } yield ()

  private def makeRef: UIO[Ref[Int]] = Ref.make(0)

}
