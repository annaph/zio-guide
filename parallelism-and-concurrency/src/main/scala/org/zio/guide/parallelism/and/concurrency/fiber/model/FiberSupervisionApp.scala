package org.zio.guide.parallelism.and.concurrency.fiber.model

import org.zio.guide.parallelism.and.concurrency.Implicits.{IntOps, StringOps}
import zio.{Console, Scope, UIO, ZIO, ZIOAppArgs, ZIOAppDefault}

object FiberSupervisionApp extends ZIOAppDefault {

  private lazy val child: UIO[Unit] =
    for {
      _ <- Console.printLine(line = "Child fiber beginning execution...".withGreenBackground).orDie
      _ <- ZIO.sleep(5.seconds)
      _ <- Console.printLine(line = "Hello from a child fiber!".withGreenBackground).orDie
    } yield ()

  private lazy val parent: UIO[Unit] =
    for {
      _ <- Console.printLine(line = "Parent fiber beginning execution...".withGreenBackground).orDie
      _ <- child.fork
      _ <- ZIO.sleep(3.seconds)
      _ <- Console.printLine(line = "Hello from a parent fiber!".withGreenBackground).orDie
    } yield ()

  private lazy val example: UIO[Unit] =
    for {
      fiber <- parent.fork
      _ <- ZIO.sleep(1.seconds)
      _ <- fiber.interrupt
      _ <- Console.printLine(line = "Parent fiber interrupted!".withMagentaBackground).orDie
      _ <- ZIO.sleep(12.seconds)
    } yield ()

  override def run: ZIO[Any with ZIOAppArgs with Scope, Nothing, Unit] = example

}
