package org.zio.guide

import zio.{Task, ZIO}

package object essentials {

  def printLine(line: String): Task[Unit] = ZIO.attempt {
    val str = s"${Console.RESET}${Console.GREEN}$line${Console.RESET}"
    Console.println(str)
  }

}
