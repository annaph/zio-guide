package org.zio.guide.concurrent.structures.ref

import org.zio.guide.concurrent.structures.Implicits.StringOps
import zio.{Console, Ref, Scope, UIO, ZIO, ZIOAppArgs, ZIOAppDefault}

object UpdateRefAndLogApp extends ZIOAppDefault {

  override def run: ZIO[Any with ZIOAppArgs with Scope, Nothing, Unit] =
    for {
      ref <- createRef
      _ <- updateAndLog(ref)(_ + 3)
    } yield ()

  private def createRef: UIO[Ref[Int]] =
    Ref.make(0)

  private def updateAndLog[T](ref: Ref[T])(f: T => T): UIO[Unit] =
    ref.modify { t =>
      val newT = f(t)
      ((t, newT), newT)
    }.flatMap {
      case (oldValue, newValue) =>
        Console.printLine(line = s"Updated '$oldValue' to '$newValue'.".withGreenBackground).orDie
    }

}
