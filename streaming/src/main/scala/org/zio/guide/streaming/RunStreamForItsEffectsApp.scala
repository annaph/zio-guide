package org.zio.guide.streaming

import org.zio.guide.streaming.Implicits.StringOps
import zio.stream.ZStream
import zio.{Console, Scope, UIO, ZIO, ZIOAppArgs, ZIOAppDefault}

object RunStreamForItsEffectsApp extends ZIOAppDefault {

  private lazy val runStream: UIO[Unit] =
    for {
      x <- ZStream(1, 2)
      y <- ZStream(x, x + 3)
    } Console.printLine(line = y.toString.withGreenBackground).orDie

  override def run: ZIO[ZIOAppArgs with Scope, Nothing, Unit] =
    runStream

}
