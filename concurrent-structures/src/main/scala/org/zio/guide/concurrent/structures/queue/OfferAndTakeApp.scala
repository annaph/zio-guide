package org.zio.guide.concurrent.structures.queue

import org.zio.guide.concurrent.structures.Implicits.StringOps
import zio.{Console, Queue, Scope, UIO, ZIO, ZIOAppArgs, ZIOAppDefault}

object OfferAndTakeApp extends ZIOAppDefault {

  private lazy val myQueueWork: UIO[Seq[Int]] =
    for {
      queue <- Queue.unbounded[Int]
      _ <- ZIO.foreach(Seq(1, 2, 3))(queue.offer)
      result <- ZIO.collectAll(ZIO.replicate(3)(queue.take))
    } yield result.toSeq

  override def run: ZIO[Any with ZIOAppArgs with Scope, Nothing, Unit] =
    for {
      result <- myQueueWork
      _ <- Console.printLine(line = s"result: ${result mkString ","}".withGreenBackground).orDie
    } yield ()

}
