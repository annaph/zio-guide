package org.zio.guide.concurrent

import scala.concurrent.duration._

package object structures {

  object Implicits {

    implicit class StringOps(str: String) {
      def withGreenBackground: String =
        s"${Console.RESET}${Console.GREEN}$str${Console.RESET}"

      def withMagentaBackground: String =
        s"${Console.RESET}${Console.MAGENTA}$str${Console.RESET}"

      def withRedBackground: String =
        s"${Console.RESET}${Console.RED}$str${Console.RESET}"
    }

    implicit class IntOps(n: Int) {
      def seconds: zio.Duration = zio.Duration.fromScala(n.second)
    }
  }

}
