package org.zio.guide.concurrent.structures.promise

import org.zio.guide.concurrent.structures.Implicits.StringOps
import zio.{Console, Scope, UIO, ZIO, ZIOAppArgs, ZIOAppDefault}

object CacheApp extends ZIOAppDefault {

  private lazy val createAndGetFromCache: UIO[Seq[String]] = {
    for {
      cache <- Cache.make[Int, Any, Nothing, String](countryLookup)
      usa <- cache.get(key = 1)
      ukFirst <- cache.get(key = 2)
      ukSecond <- cache.get(key = 2)
    } yield Seq(usa, ukFirst, ukSecond)
  }

  override def run: ZIO[Any with ZIOAppArgs with Scope, Nothing, Unit] =
    for {
      result <- createAndGetFromCache
      _ <- Console.printLine(line = s"${result mkString ","}".withGreenBackground).orDie
    } yield ()

  private def countryLookup(code: Int): UIO[String] = code match {
    case 1 => ZIO.succeed("US")
    case 2 => ZIO.succeed("Russia")
    case 3 => ZIO.succeed("UK")
    case _ => ZIO.succeed("NA")
  }

}
