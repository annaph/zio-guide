package org.zio.guide

import zio.Duration

import java.util.concurrent.TimeUnit

package object streaming {

  object Implicits {

    implicit val oneSecond: Duration = Duration(1, TimeUnit.SECONDS)

    implicit class StringOps(str: String) {
      def withGreenBackground: String =
        s"${Console.RESET}${Console.GREEN}$str${Console.RESET}"

      def withRedBackground: String =
        s"${Console.RESET}${Console.RED}$str${Console.RESET}"
    }

  }

}
