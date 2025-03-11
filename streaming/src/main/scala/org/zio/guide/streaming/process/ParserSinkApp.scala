package org.zio.guide.streaming.process

import org.zio.guide.streaming.Implicits.StringOps
import zio.stream.{ZSink, ZStream}
import zio.{Console, Scope, ZIO, ZIOAppArgs, ZIOAppDefault}

object ParserSinkApp extends ZIOAppDefault {

  private lazy val parserStream: ZStream[Any, Nothing, String] =
    ZStream("<=start=>") concat encodedStream.transduce(parserSink)

  private lazy val encodedStream: ZStream[Any, Nothing, Char] =
    ZStream(
      '3', 'a', 'b', 'c',
      '2', 'd', 'e',
      '1', 'f'
    )

  private lazy val parserSink: ZSink[Any, Nothing, Char, Char, String] =
    lengthSink.flatMap {
      case Some(n) if n.isDigit => charsSink(n.toString.toInt)
      case None => ZSink.drain.map(_ => "<=end=>")
      case _ => ZSink.dieMessage("Unexpected end of stream!")
    }

  private lazy val lengthSink: ZSink[Any, Nothing, Char, Char, Option[Char]] =
    ZSink.head

  override def run: ZIO[ZIOAppArgs with Scope, Nothing, Unit] =
    for {
      result <- parserStream.runCollect
      _ <- Console.printLine(result.map(_.withGreenBackground) mkString "\n").orDie
    } yield ()

  private def charsSink(n: Int): ZSink[Any, Nothing, Char, Char, String] =
    ZSink.collectAllN(n).map(_.mkString)

}
