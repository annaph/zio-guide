package org.zio.guide.concurrent.structures.ref

import org.zio.guide.concurrent.structures.Implicits.StringOps
import zio.{Chunk, Console, FiberRef, Scope, UIO, ZIO, ZIOAppArgs, ZIOAppDefault}

object LoggingRefApp extends ZIOAppDefault {

  private type Log = Tree[Chunk[String]]

  private lazy val loggingRef: ZIO[Scope, Nothing, FiberRef[Log]] =
    FiberRef.make(
      initial = Tree(Chunk.empty[String], List.empty[Log]),
      fork = _ => Tree(Chunk.empty[String], List.empty[Log]),
      join = (parent, child) => parent.copy(tail = child :: parent.tail)
    )

  private val createAndUpdateLoggingRef: UIO[Unit] = {
    for {
      ref <- ZIO.scoped(loggingRef)
      leftEffect = for {
        a <- ZIO.succeed(1).tap(_ => log(ref)(msg = "Got 1"))
        b <- ZIO.succeed(2).tap(_ => log(ref)(msg = "Got 2"))
      } yield a + b
      rightEffect = for {
        c <- ZIO.succeed(3).tap(_ => log(ref)(msg = "Got 3"))
        d <- ZIO.succeed(4).tap(_ => log(ref)(msg = "Got 4"))
      } yield c + d
      leftFiber <- leftEffect.fork
      rightFiber <- rightEffect.fork
      _ <- leftFiber.join
      _ <- rightFiber.join
      log <- ref.get
      _ <- Console.print(line = s"$log".withGreenBackground).orDie
    } yield ()
  }

  override def run: ZIO[Any with ZIOAppArgs with Scope, Nothing, Unit] =
    createAndUpdateLoggingRef

  private def log(loggingRef: FiberRef[Log])(msg: String): UIO[Unit] =
    loggingRef.update(log => log.copy(head = log.head.appended(msg)))

  private case class Tree[A](head: A, tail: List[Tree[A]])

}
