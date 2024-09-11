package org.zio.guide.stm.data.structures

import org.zio.guide.stm.Implicits.StringOps
import zio.stm.TReentrantLock
import zio.{Console, Scope, UIO, ZIO, ZIOAppArgs, ZIOAppDefault}

import scala.collection.mutable

object TReentrantLockActorSupervisorApp extends ZIOAppDefault {

  private val actor1 = Actor(id = 1, "actor-1")
  private val actor2 = Actor(id = 2, "actor-2")
  private val actor3 = Actor(id = 3, "actor-3")

  override def run: ZIO[ZIOAppArgs with Scope, Nothing, Unit] =
    for {
      lock1 <- TReentrantLock.make.commit
      lock2 <- TReentrantLock.make.commit
      supervisor1 <- Supervisor(name = "supervisor-1", lock1)
      supervisor2 <- Supervisor(name = "supervisor-2", lock2)
      _ <- supervisor1.supervise(actor1) *> supervisor1.supervise(actor2)
      _ <- supervisor2.supervise(actor3)
      _ <- Console.printLine(line = s"Before transfers:".withBlueBackground).orDie
      _ <- supervisor1.print
      _ <- supervisor2.print
      transfers = Seq(
        transfer(from = supervisor1, to = supervisor2, actor1),
        transfer(from = supervisor1, to = supervisor2, actor2),
        transfer(from = supervisor2, to = supervisor1, actor3)
      )
      _ <- ZIO.collectAllParDiscard(transfers)
      _ <- Console.printLine(line = s"After transfers:".withBlueBackground).orDie
      _ <- supervisor1.print
      _ <- supervisor2.print
    } yield ()

  private def transfer(from: Supervisor, to: Supervisor, actor: Actor): UIO[Unit] =
    ZIO.acquireReleaseWith(
      acquire = (from.lock.acquireWrite *> to.lock.acquireWrite).commit
    )(
      release = _ => (from.lock.releaseWrite *> to.lock.releaseWrite).commit
    )(
      use = _ => from.unsupervise(actor) *> to.supervise(actor)
    )

  private case class Actor(id: Long, name: String)

  private class Supervisor private(val name: String, val lock: TReentrantLock) {

    private val _actors = mutable.Map.empty[Long, Actor]
    private var _size = 0

    def supervise(actor: Actor): UIO[Unit] =
      ZIO.succeed {
        _actors.put(key = actor.id, value = actor) match {
          case None => _size += 1
          case _ => ()
        }
      }

    def unsupervise(actor: Actor): UIO[Unit] =
      ZIO.succeed {
        _actors.remove(key = actor.id) match {
          case Some(_) => _size -= 1
          case _ => ()
        }
      }

    def print: UIO[Unit] =
      Console
        .printLine(line = s"${this.toString}".withGreenBackground)
        .orDie

    override def toString: String =
      s"~> name: '$name'; number of actors: '${_size}'; actors => ${_actors mkString ", "}"

  }

  private object Supervisor {
    def apply(name: String, lock: TReentrantLock): UIO[Supervisor] =
      ZIO.succeed(new Supervisor(name, lock))
  }

}
