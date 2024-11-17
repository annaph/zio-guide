package org.zio.guide.advanced.error

import zio.Duration

import java.util.concurrent.TimeUnit

package object management {

  object Implicits {

    implicit val oneSecond: Duration = Duration(1, TimeUnit.SECONDS)

    implicit val threeSeconds: Duration = Duration(3, TimeUnit.SECONDS)

    implicit val sevenSeconds: Duration = Duration(7, TimeUnit.SECONDS)

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
