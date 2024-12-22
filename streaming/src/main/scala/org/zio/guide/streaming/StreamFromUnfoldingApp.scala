package org.zio.guide.streaming

import org.zio.guide.streaming.Implicits.StringOps
import zio.stream.ZStream
import zio.{Console, Scope, ZIO, ZIOAppArgs, ZIOAppDefault}

object StreamFromUnfoldingApp extends ZIOAppDefault {

  override def run: ZIO[ZIOAppArgs with Scope, Nothing, Unit] =
    fromList(List(1, 2, 3))
      .foreach { n =>
        Console.printLine(line = n.toString.withGreenBackground).orDie
      }

  private def fromList[A](list: List[A]): ZStream[Any, Nothing, A] =
    ZStream.unfold(list) {
      case head :: tail => Some(head -> tail)
      case Nil => None
    }

}
