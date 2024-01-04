package org.zio.guide.concurrent.structures.semaphore

import org.zio.guide.concurrent.structures.Implicits.{StringOps, threeSeconds}
import zio.{Clock, Console, RIO, Ref, Scope, Semaphore, ZIO, ZIOAppArgs, ZIOAppDefault}

object LimitParallelismApp extends ZIOAppDefault {

  override def run: ZIO[ZIOAppArgs with Scope, Throwable, Unit] =
    for {
      ref <- Ref.make(0)
      semaphore <- Semaphore.make(permits = 3)
      queryEffect = (requestId: Long) => semaphore.withPermit(queryDatabase(requestId, ref))
      _ <- ZIO.foreachParDiscard(1 to 11)(id => queryEffect(id.toLong))
    } yield ()

  private def queryDatabase[R](requestId: Long, numOfConnections: Ref[Int]): RIO[R, Unit] =
    for {
      preConnections <- numOfConnections.updateAndGet(_ + 1)
      _ <- Console
        .printLine(line = s"Acquiring [id: '$requestId'], now '$preConnections' connections".withBlueBackground)
      _ <- Clock sleep threeSeconds
      postConnections <- numOfConnections.updateAndGet(_ - 1)
      _ <- Console
        .printLine(line = s"Closing [id: '$requestId'], now '$postConnections' connections".withGreenBackground)
    } yield ()

}
