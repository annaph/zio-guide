package org.zio.guide

import zio.Duration

import java.util.concurrent.TimeUnit

package object stm {

  object Implicits {

    implicit val halfSecond: Duration = Duration(500, TimeUnit.MILLISECONDS)

    implicit val oneSecond: Duration = Duration(1, TimeUnit.SECONDS)

    implicit val threeSeconds: Duration = Duration(3, TimeUnit.SECONDS)

    implicit class StringOps(str: String) {
      def withGreenBackground: String =
        s"${Console.RESET}${Console.GREEN}$str${Console.RESET}"

      def withRedBackground: String =
        s"${Console.RESET}${Console.RED}$str${Console.RESET}"

      def withBlueBackground: String =
        s"${Console.RESET}${Console.BLUE}$str${Console.RESET}"
    }

  }

}
