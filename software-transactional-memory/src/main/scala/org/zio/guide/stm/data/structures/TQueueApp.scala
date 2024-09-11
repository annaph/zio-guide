package org.zio.guide.stm.data.structures

import org.zio.guide.stm.Implicits.{StringOps, oneSecond, threeSeconds}
import zio.stm.{STM, TRef, USTM}
import zio.{Console, Scope, UIO, ZIO, ZIOAppArgs, ZIOAppDefault}

import scala.collection.immutable.{Queue => ScalaQueue}

object TQueueApp extends ZIOAppDefault {

  override def run: ZIO[ZIOAppArgs with Scope, Nothing, Unit] =
    for {
      queue <- MyTQueue.bounded[Int](requestedCapacity = 2).commit
      offerElemsFiber <- offerElems(queue).fork
      peekElemFiber <- peekElem(queue).fork
      takeElemsFiber <- takeElems(queue).fork
      _ <- offerElemsFiber.await
      _ <- peekElemFiber.await
      _ <- takeElemsFiber.await
      _ <- poolElem(queue)
    } yield ()

  private def offerElems(queue: MyTQueue[Int]): UIO[Unit] =
    ZIO.foreachDiscard(Seq(1, 2, 3))(n => queue.offer(n).commit)

  private def takeElems(queue: MyTQueue[Int]): UIO[Unit] =
    ZIO.sleep(threeSeconds) *>
      ZIO.foreach(Seq(1, 2, 3))(_ => queue.take.commit)
        .flatMap { elems =>
          Console
            .printLine(line = s"Elements taken from queue: ${elems mkString ", "}".withGreenBackground)
            .orDie
        }

  private def peekElem(queue: MyTQueue[Int]): UIO[Unit] =
    ZIO.sleep(oneSecond) *>
      queue
        .peek
        .commit
        .flatMap { peeked =>
          Console
            .printLine(line = s"Element peeked: $peeked".withGreenBackground)
            .orDie
        }

  private def poolElem(queue: MyTQueue[Int]): UIO[Unit] =
    queue
      .poll
      .commit
      .flatMap { pooled =>
        Console.printLine(line = s"Element pooled: $pooled".withGreenBackground).orDie
      }

  final class MyTQueue[A] private(private val capacity: Int, private val ref: TRef[ScalaQueue[A]]) {

    def offer(a: A): USTM[Unit] =
      ref.get.flatMap { queue =>
        if (queue.size == capacity) STM.retry
        else ref.set(queue.enqueue(a))
      }

    def peek: USTM[A] =
      ref.get.flatMap { queue =>
        if (queue.isEmpty) STM.retry
        else STM.succeed(queue.head)
      }

    def take: USTM[A] =
      ref.get.flatMap { queue =>
        if (queue.isEmpty) STM.retry
        else queue.dequeue match {
          case (a, newQueue) => ref.set(newQueue).as(a)
        }
      }

    def poll: USTM[Option[A]] =
      ref.get.flatMap { queue =>
        if (queue.isEmpty) STM.succeed(Option.empty[A])
        else queue.dequeue match {
          case (a, newQueue) => ref.set(newQueue).as(Some(a))
        }
      }

  }

  private object MyTQueue {
    def bounded[A](requestedCapacity: => Int): USTM[MyTQueue[A]] =
      TRef
        .make(ScalaQueue.empty[A])
        .map(new MyTQueue(requestedCapacity, _))
  }

}
