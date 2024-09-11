package org.zio.guide.stm.data.structures

import org.zio.guide.stm.Implicits.{StringOps, threeSeconds}
import zio.stm.{STM, TRef, USTM}
import zio.{Console, Scope, UIO, ZIO, ZIOAppArgs, ZIOAppDefault}

object TPromiseApp extends ZIOAppDefault {

  override def run: ZIO[ZIOAppArgs with Scope, Nothing, Unit] =
    for {
      promise <- MyTPromise.make[Throwable, Int].commit
      _ <- printPromise(title = "After creation", promise)
      completePromise = ZIO.sleep(threeSeconds) *> promise.done(Right(3)).commit
      fiber <- completePromise.fork
      _ <- printPromise(title = "Before awaiting", promise)
      _ <- promise.await.commit.orDie
      _ <- printPromise(title = "After done awaiting", promise)
      _ <- fiber.await
    } yield ()

  private def printPromise[E, A](title: String, promise: MyTPromise[E, A]): UIO[Unit] =
    promise
      .poll
      .map(option => s"~> $title: $option")
      .commit
      .flatMap { str =>
        Console.printLine(line = s"$str".withGreenBackground).orDie
      }

  final class MyTPromise[E, A] private(private val ref: TRef[Option[Either[E, A]]]) {
    def await: STM[E, A] =
      ref.get.flatMap {
        case Some(value) =>
          STM.fromEither(value)
        case None =>
          STM.retry
      }

    def done(value: Either[E, A]): USTM[Boolean] =
      ref.get.flatMap {
        case Some(_) =>
          STM.succeed(false)
        case None =>
          ref.set(Some(value)) *> STM.succeed(true)
      }

    def poll: USTM[Option[Either[E, A]]] = ref.get

  }

  private object MyTPromise {
    def make[E, A]: USTM[MyTPromise[E, A]] =
      TRef
        .make(Option.empty[Either[E, A]])
        .map(new MyTPromise(_))
  }

}
