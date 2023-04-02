package org.zio.guide.essentials.first

import zio.{Task, ZIO}

package object steps {

  def printLine(line: String): Task[Unit] = ZIO.attempt {
    val str = s"${Console.RESET}${Console.GREEN}$line${Console.RESET}"
    Console.println(str)
  }

}
