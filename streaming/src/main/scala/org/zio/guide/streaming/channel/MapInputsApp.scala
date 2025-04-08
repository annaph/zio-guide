package org.zio.guide.streaming.channel

import org.zio.guide.streaming.Implicits.StringOps
import zio.stream.{ZChannel, ZPipeline, ZSink, ZStream}
import zio.{Chunk, Console, Scope, ZIO, ZIOAppArgs, ZIOAppDefault}

object MapInputsApp extends ZIOAppDefault {

  private lazy val channel: ZChannel[Any, Nothing, Chunk[String], Any, Throwable, Chunk[Int], Any] =
    mapInput[Throwable, Chunk[String], Chunk[Int], Any](_.map(_.toInt * 3))

  private lazy val stream: ZStream[Any, Nothing, String] =
    ZStream("1", "2", "3")

  private lazy val pipeline: ZPipeline[Any, Throwable, String, Int] = channel.toPipeline

  private val sink: ZSink[Any, Nothing, Int, Nothing, Unit] =
    ZSink.foreach { in => Console.printLine(in.toString.withGreenBackground).orDie }

  override def run: ZIO[ZIOAppArgs with Scope, Throwable, Unit] =
    stream >>> pipeline >>> sink

  private def mapInput[Err, In, Out, Done](f: In => Out): ZChannel[Any, Err, In, Done, Err, Out, Done] =
    ZChannel.readWith(
      in = inElem => ZChannel.write(f(inElem)) *> mapInput(f),
      error = inError => ZChannel.fail(inError),
      done = inDone => ZChannel.succeed(inDone)
    )

}
