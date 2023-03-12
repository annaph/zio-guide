package org.zio.guide.essentials

import zio.{Scope, Task, ZIO, ZIOAppArgs, ZIOAppDefault}

object PrintNumbers extends ZIOAppDefault {

  private val printNumbers1: Task[Seq[Unit]] = ZIO.foreach(Seq(1, 2, 3)) { i =>
    printLine(i.toString)
  }

  private val printNumbers2: Task[Seq[Unit]] = ZIO.collectAll(
    Seq(
      printLine(line = "4"),
      printLine(line = "5"),
      printLine(line = "6")
    )
  )

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Seq[Unit]] =
    printNumbers1 *> printNumbers2

}
