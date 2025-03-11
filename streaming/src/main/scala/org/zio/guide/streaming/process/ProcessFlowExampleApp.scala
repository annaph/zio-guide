package org.zio.guide.streaming.process

import org.zio.guide.streaming.Implicits.StringOps
import zio.stream.{ZPipeline, ZSink, ZStream}
import zio.{Console, Scope, ZIO, ZIOAppArgs, ZIOAppDefault}

object ProcessFlowExampleApp extends ZIOAppDefault {

  private lazy val stream: ZStream[Any, Nothing, Int] =
    ZStream(1, 2, 3)

  private lazy val pipeline: ZPipeline[Any, Nothing, Int, String] =
    ZPipeline.map { in => (2 * in).toString }

  private val sink: ZSink[Any, Nothing, String, Nothing, Unit] =
    ZSink.foreach { in => Console.printLine(in.withGreenBackground).orDie }

  override def run: ZIO[ZIOAppArgs with Scope, Any, Any] =
    stream >>> pipeline >>> sink

}
