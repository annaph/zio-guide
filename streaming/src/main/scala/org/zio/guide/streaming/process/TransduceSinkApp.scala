package org.zio.guide.streaming.process

import org.zio.guide.streaming.Implicits.StringOps
import zio.stream.{ZSink, ZStream}
import zio.{Chunk, Console, Scope, ZIO, ZIOAppArgs, ZIOAppDefault}

object TransduceSinkApp extends ZIOAppDefault {

  private lazy val stream: ZStream[Any, Nothing, Int] =
    ZStream(1, 2, 3, 4, 5)

  private val sink: ZSink[Any, Nothing, Int, Int, Chunk[Int]] =
    ZSink.collectAllN(n = 3)

  private val transduceStream: ZStream[Any, Nothing, Chunk[Int]] =
    stream.transduce(sink)

  override def run: ZIO[ZIOAppArgs with Scope, Nothing, Unit] =
    for {
      result <- transduceStream.runCollect
      _ <- Console.printLine(formatResult(result) mkString "\n").orDie
    } yield ()

  private def formatResult(result: Chunk[Chunk[Int]]): Chunk[String] =
    result.map {
      _.mkString("Chunk(", ",", ")").withGreenBackground
    }

}
