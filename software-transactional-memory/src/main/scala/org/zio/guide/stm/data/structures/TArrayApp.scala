package org.zio.guide.stm.data.structures

import org.zio.guide.stm.Implicits.StringOps
import zio.stm.{TArray, USTM}
import zio.{Console, Scope, UIO, ZIO, ZIOAppArgs, ZIOAppDefault}

object TArrayApp extends ZIOAppDefault {

  override def run: ZIO[ZIOAppArgs with Scope, Nothing, Unit] =
    for {
      array <- TArray.make(data = 1, 2, 3).commit
      _ <- printArray(title = "After creation", array)
      _ <- swap(array, i = 0, j = 2).commit
      _ <- swap(array, i = 2, j = 1).commit
      _ <- printArray(title = "After swap", array)
      _ <- incrementAll(array).commit
      _ <- printArray(title = "After increment all", array)
    } yield ()

  private def swap[T](array: TArray[T], i: Int, j: Int): USTM[Unit] =
    for {
      x <- array(i)
      y <- array(j)
      _ <- array.update(i, _ => y)
      _ <- array.update(j, _ => x)
    } yield ()

  private def incrementAll(array: TArray[Int]): USTM[Unit] =
    array.transform(_ + 1)

  private def printArray(title: String, array: TArray[Int]): UIO[Unit] =
    array
      .toList
      .map(list => s"~> $title: ${list.mkString("[", ",", "]")}")
      .commit
      .flatMap { str =>
        Console.printLine(line = s"$str".withGreenBackground).orDie
      }

}
