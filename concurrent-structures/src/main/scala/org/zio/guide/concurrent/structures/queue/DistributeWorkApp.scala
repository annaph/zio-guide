package org.zio.guide.concurrent.structures.queue

import org.zio.guide.concurrent.structures.Implicits._
import zio.{Clock, Console, Queue, Scope, UIO, ZIO, ZIOAppArgs, ZIOAppDefault}

object DistributeWorkApp extends ZIOAppDefault {

  private lazy val myQueueWork: UIO[Unit] =
    for {
      queue <- Queue.unbounded[Int]
      _ <- worker(queue)(id = "left", color = "green").fork
      _ <- worker(queue)(id = "right", color = "blue").fork
      _ <- ZIO.foreachDiscard(as = 1 to 10)(queue.offer)
      _ <- Clock sleep oneMinute
    } yield ()

  override def run: ZIO[Any with ZIOAppArgs with Scope, Nothing, Unit] = myQueueWork

  private def worker(queue: Queue[Int])(id: String, color: String): UIO[Unit] =
    queue
      .take
      .flatMap(work(WorkerInfo(id, color)))
      .forever

  private def work(workerInfo: WorkerInfo)(n: Int): UIO[Unit] =
    for {
      _ <- printLine(line = s"~~> fiber '${workerInfo.id}' starting work on '$n'", workerInfo.color)
      _ <- Clock sleep oneSecond
      _ <- printLine(line = s"==> fiber ${workerInfo.id} finished with work on '$n'", workerInfo.color)
    } yield ()

  private def printLine(line: String, color: String): UIO[Unit] = {
    lazy val formattedLine = color match {
      case "green" => line.withGreenBackground
      case "blue" => line.withBlueBackground
      case _ => line
    }

    Console
      .printLine(formattedLine)
      .orDie
  }

  private case class WorkerInfo(id: String, color: String)

}
