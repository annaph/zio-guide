package org.zio.guide.concurrent.structures.promise

import org.zio.guide.concurrent.structures.Implicits.StringOps
import zio.{Console, Promise, Scope, UIO, ZIO, ZIOAppArgs, ZIOAppDefault}

object SimplePromiseApp extends ZIOAppDefault {

  private val printHelloMsg: UIO[Unit] = {
    for {
      promise <- Promise.make[Nothing, Unit]
      leftEffect = Console.print(line = "Hello, ".withGreenBackground).orDie *> promise.succeed(())
      rightEffect = promise.await *> Console.print(line = "World!".withGreenBackground).orDie
      leftFiber <- leftEffect.fork
      rightFiber <- rightEffect.fork
      _ <- leftFiber.join
      _ <- rightFiber.join
    } yield ()
  }

  override def run: ZIO[Any with ZIOAppArgs with Scope, Nothing, Unit] = printHelloMsg

}
