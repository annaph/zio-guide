package org.zio.guide.streaming.process

import org.zio.guide.streaming.Implicits.StringOps
import zio.stream.{ZSink, ZStream}
import zio.{Console, Scope, ZIO, ZIOAppArgs, ZIOAppDefault}

import java.io.File

object CopySinkApp extends ZIOAppDefault {

  private lazy val readFromFile: ZStream[Any, Throwable, Byte] =
    ZStream.fromFile(new File("src/main/resources/file.txt"))

  private lazy val writeToFile: ZSink[Any, Throwable, Byte, Byte, Long] =
    ZSink.fromFile(new File("src/main/resources/file_copy.txt"))

  override def run: ZIO[ZIOAppArgs with Scope, Throwable, Unit] =
    for {
      summary <- readFromFile >>> writeToFile
      _ <- Console.printLine(line = s"Number of written bytes: $summary".withGreenBackground).orDie
    } yield ()

}
