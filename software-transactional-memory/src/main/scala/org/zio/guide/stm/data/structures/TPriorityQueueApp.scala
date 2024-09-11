package org.zio.guide.stm.data.structures

import org.zio.guide.stm.Implicits.{StringOps, halfSecond, oneSecond, threeSeconds}
import zio.stm.{STM, TRef, USTM}
import zio.{Console, Scope, UIO, ZIO, ZIOAppArgs, ZIOAppDefault}

import scala.collection.immutable.SortedMap

object TPriorityQueueApp extends ZIOAppDefault {

  private implicit val eventOrdering: Ordering[Event] = Ordering.by(_.priority)

  private val eventA = Event(
    id = 1L,
    priority = 2,
    action = Console.printLine(line = s"1 - Event A/2".withGreenBackground).orDie
  )

  private val eventB = Event(
    id = 2L,
    priority = 2,
    action = Console.printLine(line = s"3 - Event B/2".withGreenBackground).orDie
  )

  private val eventC = Event(
    id = 3L,
    priority = 3,
    action = Console.printLine(line = s"4 - Event C/3".withGreenBackground).orDie
  )

  private val eventD = Event(
    id = 4L,
    priority = 1,
    action = Console.printLine(line = s"2 - Event D/1".withGreenBackground).orDie
  )

  override def run: ZIO[ZIOAppArgs with Scope, Nothing, Unit] =
    for {
      queue <- MyTPriorityQueue.empty[Event].commit
      takeEventsFiber <- takeEvents(queue).fork
      _ <- ZIO sleep oneSecond
      offerEventsFiber <- offerEvents(events = Seq(eventA, eventB, eventC, eventD), queue).fork
      _ <- takeEventsFiber.await
      _ <- offerEventsFiber.await
    } yield ()

  private def offerEvents(events: Seq[Event], queue: MyTPriorityQueue[Event]): UIO[Unit] =
    ZIO.foreachDiscard(events)(queue.offer(_).commit *> ZIO.sleep(halfSecond))

  private def takeEvents(queue: MyTPriorityQueue[Event]): UIO[Unit] =
    ZIO.foreachDiscard(Seq(1, 2, 3, 4))(_ => queue.take.commit.flatMap(_.action) *> ZIO.sleep(threeSeconds))

  final class MyTPriorityQueue[A] private(ref: TRef[SortedMap[A, ::[A]]]) {

    def offer(a: A): USTM[Unit] =
      ref.get.flatMap { map =>
        map.get(a) match {
          case Some(as) =>
            val updatedEntry = a -> ::(a, as)
            ref.set(map + updatedEntry)
          case None =>
            val newEntry = a -> ::(a, Nil)
            ref.set(map + newEntry)
        }
      }

    def take: USTM[A] =
      ref.get.flatMap { map =>
        map.headOption match {
          case Some((a, head :: Nil)) =>
            ref.set(map - a).as(head)
          case Some((a, head :: newHead :: tail)) =>
            val updatedEntry = a -> ::(newHead, tail)
            ref.set(map + updatedEntry).as(head)
          case None =>
            STM.retry
        }
      }

  }

  private case class Event(id: Long, priority: Int, action: UIO[Unit])

  private object MyTPriorityQueue {
    def empty[A](implicit ord: Ordering[A]): USTM[MyTPriorityQueue[A]] =
      TRef
        .make(SortedMap.empty[A, ::[A]])
        .map(new MyTPriorityQueue(_))
  }

}
