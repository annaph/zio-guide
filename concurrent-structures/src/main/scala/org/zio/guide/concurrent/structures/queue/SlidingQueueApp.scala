package org.zio.guide.concurrent.structures.queue

import org.zio.guide.concurrent.structures.Implicits.StringOps
import zio.{Console, Queue, Scope, UIO, ZIO, ZIOAppArgs, ZIOAppDefault}

object SlidingQueueApp extends ZIOAppDefault {

  private lazy val mySlidingQueueWork: UIO[(Int, Int)] =
    for {
      queue <- Queue.sliding[Int](requestedCapacity = 2)
      _ <- ZIO.foreachDiscard(Seq(1, 2, 3))(queue.offer)
      x <- queue.take
      y <- queue.take
    } yield (x, y)

  override def run: ZIO[ZIOAppArgs with Scope, Nothing, Unit] =
    for {
      result <- mySlidingQueueWork
      (x, y) = result
      _ <- Console.printLine(line = s"result: ($x, $y)".withGreenBackground).orDie
    } yield ()

}
