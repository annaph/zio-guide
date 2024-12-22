package org.zio.guide.streaming

import org.zio.guide.streaming.Implicits.StringOps
import zio.stream.ZStream
import zio.{Console, Scope, ZIO, ZIOAppArgs, ZIOAppDefault}

object StreamFromIterableApp extends ZIOAppDefault {

  private lazy val stream: ZStream[Any, Nothing, Int] = ZStream.fromIterable(Seq(1, 2, 3))
  
  override def run: ZIO[ZIOAppArgs with Scope, Nothing, Unit] =
    stream
      .foreach(n => Console.printLine(line = s"Stream value: $n".withGreenBackground))
      .orDie

}
