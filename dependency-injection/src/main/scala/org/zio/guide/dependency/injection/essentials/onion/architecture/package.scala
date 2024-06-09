package org.zio.guide.dependency.injection.essentials.onion

import zio.Duration

import java.util.concurrent.TimeUnit

package object architecture {

  object Implicits {

    implicit val twoSeconds: Duration = Duration(2, TimeUnit.SECONDS)

    implicit val threeSeconds: Duration = Duration(3, TimeUnit.SECONDS)

    implicit class StringOps(str: String) {
      def withGreenBackground: String =
        s"${Console.RESET}${Console.GREEN}$str${Console.RESET}"

      def withBlueBackground: String =
        s"${Console.RESET}${Console.BLUE}$str${Console.RESET}"

      def withCyanBackground: String =
        s"${Console.RESET}${Console.CYAN}$str${Console.RESET}"
    }

  }

}
