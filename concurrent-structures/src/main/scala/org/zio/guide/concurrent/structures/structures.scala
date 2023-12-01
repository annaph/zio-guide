package org.zio.guide.concurrent

import zio.Duration

import java.util.concurrent.TimeUnit

package object structures {

  object Implicits {

    implicit val oneSecond: Duration = Duration(1, TimeUnit.SECONDS)

    implicit val threeSeconds: Duration = Duration(3, TimeUnit.SECONDS)

    implicit val oneMinute: Duration = Duration(1, TimeUnit.MINUTES)

    implicit class StringOps(str: String) {
      def withGreenBackground: String =
        s"${Console.RESET}${Console.GREEN}$str${Console.RESET}"

      def withBlueBackground: String =
        s"${Console.RESET}${Console.BLUE}$str${Console.RESET}"
    }
  }

}
