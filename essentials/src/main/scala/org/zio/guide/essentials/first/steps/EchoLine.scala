package org.zio.guide.essentials.first.steps

import zio.{Scope, Task, ZIO, ZIOAppArgs, ZIOAppDefault}

import scala.io.StdIn

object EchoLine extends ZIOAppDefault {

  private val readLine: Task[String] = ZIO.attempt {
    println("Type line:")
    StdIn.readLine()
  }

  private val printLine: String => Task[Unit] = line =>
    ZIO.attempt {
      println(line)
    }

  private val echoLine: Task[Unit] =
    for {
      line <- readLine
      _ <- printLine(line)
    } yield ()

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] = echoLine

}
