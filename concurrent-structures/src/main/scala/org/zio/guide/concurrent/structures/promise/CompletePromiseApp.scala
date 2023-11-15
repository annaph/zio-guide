package org.zio.guide.concurrent.structures.promise

import org.zio.guide.concurrent.structures.Implicits.StringOps
import zio.{Console, Promise, Scope, UIO, ZIO, ZIOAppArgs, ZIOAppDefault}

import scala.util.Random

object CompletePromiseApp extends ZIOAppDefault {

  private val randomInt: UIO[Int] = ZIO.succeed {
    Random.nextInt()
  }

  private val completeEffect: UIO[(Int, Int)] =
    for {
      promise <- Promise.make[Nothing, Int]
      _ <- promise.complete(randomInt)
      l <- promise.await
      r <- promise.await
    } yield (l, r)

  private val completeWithEffect: UIO[(Int, Int)] =
    for {
      promise <- Promise.make[Nothing, Int]
      _ <- promise.completeWith(randomInt)
      l <- promise.await
      r <- promise.await
    } yield (l, r)

  override def run: ZIO[Any with ZIOAppArgs with Scope, Nothing, Unit] = {
    for {
      result1 <- completeEffect
      result2 <- completeWithEffect
      (l1, r1) = result1
      (l2, r2) = result2
      _ <- Console.printLine(line = s"l1: $l1; r1: $r1".withGreenBackground).orDie
      _ <- Console.printLine(line = s"l2: $l2; r2: $r2".withGreenBackground).orDie
    } yield ()
  }

}
