package org.zio.guide.essentials

package object exercises {

  object Implicits {

    implicit class StringOps(str: String) {
      def withGreenBackground: String =
        s"${Console.RESET}${Console.GREEN}$str${Console.RESET}"

      def withRedBackground: String =
        s"${Console.RESET}${Console.RED}$str${Console.RESET}"
    }
  }

}
