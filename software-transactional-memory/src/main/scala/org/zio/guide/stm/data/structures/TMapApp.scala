package org.zio.guide.stm.data.structures

import org.zio.guide.stm.Implicits.StringOps
import zio.stm.{STM, TMap, USTM}
import zio.{Console, Scope, UIO, ZIO, ZIOAppArgs, ZIOAppDefault}

object TMapApp extends ZIOAppDefault {

  override def run: ZIO[ZIOAppArgs with Scope, Nothing, Unit] =
    for {
      map <- TMap.make(data = 1 -> 'a', 2 -> 'b').commit
      _ <- printMap(title = "After creation", map)
      value1 <- getOrElseUpdate(map)(key = 1, value = '-').commit
      _ <- printMapEntry(key = 1, value1)
      value2 <- getOrElseUpdate(map)(key = 3, value = 'c').commit
      _ <- printMapEntry(key = 3, value2)
      _ <- printMap(title = "After get/update", map)
    } yield ()

  private def getOrElseUpdate[K, V](map: TMap[K, V])(key: K, value: => V): USTM[V] =
    map
      .get(key)
      .flatMap {
        case Some(v) => STM.succeed(v)
        case None => map.put(key, value).as(value)
      }

  private def printMap(title: String, map: TMap[Int, Char]): UIO[Unit] =
    map
      .toList
      .map(list => s"~> $title: ${list.mkString("[", ", ", "]")}")
      .commit
      .flatMap { str =>
        Console.printLine(line = s"$str".withGreenBackground).orDie
      }

  private def printMapEntry(key: Int, value: Char): UIO[Unit] =
    Console
      .printLine(line = s"~> Entry: key=$key | value=$value".withBlueBackground)
      .orDie

}
