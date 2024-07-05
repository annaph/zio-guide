package org.zio.guide.stm.composing

import zio.Duration

import java.util.concurrent.TimeUnit

package object atomicity {

  object Implicits {

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
