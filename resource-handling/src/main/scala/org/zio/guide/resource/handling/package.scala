package org.zio.guide.resource

import org.zio.guide.resource.handling.Implicits.StringOps
import zio.{Duration, Task, UIO, ZIO, Console => ZIOConsole}

import java.io.{BufferedReader, BufferedWriter, File, FileReader, FileWriter}
import java.util.concurrent.TimeUnit
import scala.annotation.tailrec

package object handling {

  def openReader(pathname: String): Task[BufferedReader] = ZIO.attempt {
    val file = new File(pathname)
    val fileReader = new FileReader(file)
    new BufferedReader(fileReader)
  }.tap { _ =>
    ZIOConsole.printLine(line = s"Reader for '$pathname' opened.".withBlueBackground).orDie
  }

  def closeReader(reader: BufferedReader): UIO[Unit] =
    ZIO
      .attempt(reader.close())
      .tap(_ => ZIOConsole.printLine(line = s"Reader closed.".withBlueBackground).orDie)
      .tapError { ex =>
        ZIOConsole.printLine(line = s"Error closing reader: ${ex.getMessage}".withRedBackground).orDie
      }.orDie

  def openWriter(pathname: String): Task[BufferedWriter] = ZIO.attempt {
    val file = new File(pathname)
    val fileWriter = new FileWriter(file)
    new BufferedWriter(fileWriter)
  }.tap { _ =>
    ZIOConsole.printLine(line = s"Writer for '$pathname' opened.".withBlueBackground).orDie
  }

  def closeWriter(writer: BufferedWriter): UIO[Unit] =
    ZIO
      .attempt(writer.close())
      .tap(_ => ZIOConsole.printLine(line = s"Writer closed.".withBlueBackground).orDie)
      .tapError {
        ex => ZIOConsole.printLine(line = s"Error closing writer: ${ex.getMessage}".withRedBackground).orDie
      }
      .orDie

  def analyze(weatherData: BufferedReader, results: BufferedWriter): Task[Unit] = ZIO.attempt {
    @tailrec
    def go(week: Int = 0, day: Int = 0, sum: Float = 0.0f): Unit =
      Option(weatherData.readLine()) match {
        case Some(line) if day == 6 =>
          val temperature = line.split(":").last.trim.toFloat

          val str = s"week ${week + 1}: ${(sum + temperature) / 7}\n"
          results.write(str)

          go(week + 1)

        case Some(line) =>
          val temperature = line.split(":").last.trim.toFloat
          go(week, day + 1, sum + temperature)

        case None if week == 0 && day == 0 =>
          ()

        case None =>
          val str = s"week ${week + 1}: ${sum / day}\n"
          results.write(str)
      }

    go()
  }

  object Implicits {

    implicit val oneSecond: Duration = Duration(1, TimeUnit.SECONDS)

    implicit val oneMinute: Duration = Duration(1, TimeUnit.MINUTES)

    implicit val sevenSeconds: Duration = Duration(7, TimeUnit.SECONDS)

    implicit class StringOps(str: String) {
      def withGreenBackground: String =
        s"${Console.RESET}${Console.GREEN}$str${Console.RESET}"

      def withBlueBackground: String =
        s"${Console.RESET}${Console.BLUE}$str${Console.RESET}"

      def withRedBackground: String =
        s"${Console.RESET}${Console.RED}$str${Console.RESET}"
    }
  }

}
