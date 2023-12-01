package org.zio.guide.concurrent.structures.queue

import org.zio.guide.concurrent.structures.Implicits.{StringOps, oneMinute}
import zio.{Clock, Console, Queue, Scope, UIO, ZIO, ZIOAppArgs, ZIOAppDefault}

object BoundedQueueApp extends ZIOAppDefault {

  override def run: ZIO[ZIOAppArgs with Scope, Nothing, Unit] =
    for {
      appArgs <- ZIO.service[ZIOAppArgs]
      offerValue = appArgs.getArgs.headOption.getOrElse("Anna")
      _ <- myBoundedQueueWork(offerValue)
      _ <- Clock sleep oneMinute
    } yield ()

  private def myBoundedQueueWork(offerValue: String): UIO[Unit] =
    for {
      queue <- Queue.bounded[String](requestedCapacity = 2)
      child = queue
        .offer(offerValue)
        .tap(_ => Console.printLine(line = s"$offerValue".withGreenBackground))
        .forever
      _ <- child.fork
    } yield ()

}
