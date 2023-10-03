package org.zio.guide.concurrent.structures.ref

import org.zio.guide.concurrent.structures.Implicits.StringOps
import zio.{Console, Scope, UIO, ZIO, ZIOAppArgs, ZIOAppDefault}

object MyRefCacheApp extends ZIOAppDefault {

  private lazy val createAndUpdateRefCache: UIO[(String, String, String)] =
    for {
      refCache <- RefCache.make[Int, String]
      ref1 <- refCache.getOrElseCompute(1)(_ => "A")
      ref2 <- refCache.getOrElseCompute(2)(_ => "B")
      ref3 <- refCache.getOrElseCompute(1)(_ => "a")
      value1 <- ref1.get
      value2 <- ref2.get
      value3 <- ref3.get
    } yield (value1, value2, value3)

  override def run: ZIO[Any with ZIOAppArgs with Scope, Nothing, Unit] =
    createAndUpdateRefCache.flatMap {
      case (value1, value2, value3) =>
        Console.printLine(line = s"value1: '$value1', value2: '$value2', value3: '$value3'".withGreenBackground).orDie
    }

}
