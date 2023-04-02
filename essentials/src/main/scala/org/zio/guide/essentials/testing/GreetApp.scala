package org.zio.guide.essentials.testing

import org.zio.guide.essentials.first.steps.exercises.Implicits.StringOps
import zio.{Console, Scope, UIO, ZIO, ZIOAppArgs, ZIOAppDefault}

object GreetApp extends ZIOAppDefault {

  private[testing] val greet: UIO[Unit] =
    for {
      name <- Console.readLine.orDie
      _ <- Console.printLine(line = s"Hello, $name!".withGreenBackground).orDie
    } yield ()

  override def run: ZIO[Any with ZIOAppArgs with Scope, Nothing, Unit] = greet

}
