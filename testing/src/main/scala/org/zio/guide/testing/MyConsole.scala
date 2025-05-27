package org.zio.guide.testing

import zio.IO

import java.io.IOException

trait MyConsole {
  def printLine(line: String): IO[IOException, Unit]

  def readLine: IO[IOException, String]
}

