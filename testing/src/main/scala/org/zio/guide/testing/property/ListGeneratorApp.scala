package org.zio.guide.testing.property

import org.zio.guide.testing.debug
import zio.test.Gen
import zio.{Scope, ZIO, ZIOAppArgs, ZIOAppDefault}

object ListGeneratorApp extends ZIOAppDefault {

  private lazy val genList: Gen[Any, List[Int]] = listOfN(n = 3)(Gen.int(min = 1, max = 12))

  override def run: ZIO[ZIOAppArgs with Scope, Nothing, Unit] =
    debug {
      genList
    }

  def listOfN[R, A](n: Int)(gen: Gen[R, A]): Gen[R, List[A]] = {
    def go(i: Int, list: List[A]): Gen[R, List[A]] = {
      gen.flatMap { a =>
        if (i == 1) Gen.const(a :: list)
        else go(i - 1, a :: list)
      }
    }

    go(n, List.empty[A])
  }

}
