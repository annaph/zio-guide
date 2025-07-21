package org.zio.guide

import org.zio.guide.testing.Implicits.StringOps
import zio.test.Gen
import zio.{ZIO, Console => ZConsole}

package object testing {

  def debug[R, A](gen: Gen[R, A]): ZIO[R, Nothing, Unit] =
    for {
      sample <- gen.runCollectN(n = 7)
      _ <- ZConsole.printLine(line = s"Generated sample\n${sample mkString "\n"}".withGreenBackground).orDie
    } yield ()

  object Implicits {

    implicit class StringOps(str: String) {
      def withGreenBackground: String =
        s"${Console.RESET}${Console.GREEN}$str${Console.RESET}"
    }

  }

}
