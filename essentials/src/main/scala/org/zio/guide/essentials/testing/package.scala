package org.zio.guide.essentials

import scala.concurrent.duration._

package object testing {

  object Implicits {

    implicit class IntOps(n: Int) {
      def seconds: zio.Duration = zio.Duration.fromScala(n.second)
    }

  }

}
