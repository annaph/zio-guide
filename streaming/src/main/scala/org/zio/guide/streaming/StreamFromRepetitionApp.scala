package org.zio.guide.streaming

import org.zio.guide.streaming.Implicits.{StringOps, oneSecond}
import zio.stream.ZStream
import zio.{Console, Ref, Scope, ZIO, ZIOAppArgs, ZIOAppDefault}

object StreamFromRepetitionApp extends ZIOAppDefault {

  override def run: ZIO[ZIOAppArgs with Scope, Nothing, Unit] =
    positiveIntegers
      .take(7)
      .foreach { n =>
        Console.printLine(line = n.toString.withGreenBackground).orDie
      }

  private def positiveIntegers: ZStream[Any, Nothing, Int] =
    ZStream
      .fromZIO(Ref.make(1))
      .flatMap { ref =>
        ZStream.repeatZIO {
          ZIO.sleep(oneSecond) *> ref.getAndUpdate(_ + 1)
        }
      }

}
