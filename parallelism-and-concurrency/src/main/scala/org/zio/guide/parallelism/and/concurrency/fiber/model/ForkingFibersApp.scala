package org.zio.guide.parallelism.and.concurrency.fiber.model

import org.zio.guide.parallelism.and.concurrency.Implicits.StringOps
import zio.{Scope, UIO, ZIO, ZIOAppArgs, ZIOAppDefault}

object ForkingFibersApp extends ZIOAppDefault {

  private lazy val doSomething: UIO[Unit] = ZIO.succeed {
    Thread sleep (3 * 1000)
    println("did something".withGreenBackground)
  }

  private lazy val doSomethingElse: UIO[Unit] = ZIO.succeed {
    Thread sleep (1 * 1000)
    println("did something else".withGreenBackground)
  }

  private lazy val example1: UIO[Unit] =
    for {
      _ <- doSomething
      _ <- doSomethingElse
    } yield ()

  private lazy val example2: UIO[Unit] =
    for {
      _ <- doSomething.fork
      _ <- doSomethingElse
    } yield ()

  override def run: ZIO[Any with ZIOAppArgs with Scope, Nothing, Unit] =
    example1 *> example2

}
