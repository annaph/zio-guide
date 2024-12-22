package org.zio.guide.streaming

import org.zio.guide.streaming.Implicits.StringOps
import zio.stream.ZStream
import zio.{Console, Scope, UIO, ZIO, ZIOAppArgs, ZIOAppDefault}

object StreamFromEffectApp extends ZIOAppDefault {

  private lazy val helloStream: ZStream[Any, Nothing, Unit] =
    ZStream.fromZIO(printToConsole(str = "hello")) ++
      ZStream.fromZIO(printToConsole(str = "from")) ++
      ZStream.fromZIO(printToConsole(str = "a")) ++
      ZStream.fromZIO(printToConsole(str = "stream"))

  override def run: ZIO[ZIOAppArgs with Scope, Nothing, Unit] =
    helloStream.foreach(_ => ZIO.unit)

  private def printToConsole(str: String): UIO[Unit] =
    Console.printLine(line = str.withGreenBackground).orDie

}
