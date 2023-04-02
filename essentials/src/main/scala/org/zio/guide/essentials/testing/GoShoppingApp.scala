package org.zio.guide.essentials.testing

import org.zio.guide.essentials.testing.Implicits.IntOps
import zio.{Console, Scope, UIO, ZIO, ZIOAppArgs, ZIOAppDefault}

object GoShoppingApp extends ZIOAppDefault {

  private[testing] val goShopping: UIO[Unit] =
    Console
      .printLine(line = "Going shopping!")
      .orDie
      .delay(7.seconds)

  override def run: ZIO[Any with ZIOAppArgs with Scope, Nothing, Unit] = goShopping

}
