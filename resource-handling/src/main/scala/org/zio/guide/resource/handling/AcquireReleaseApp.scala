package org.zio.guide.resource.handling

import org.zio.guide.resource.handling.{analyzeWeatherData => analyzeWeatherDataFiles}
import zio.{Scope, Task, ZIO, ZIOAppArgs, ZIOAppDefault}

import java.io.{BufferedReader, BufferedWriter}

object AcquireReleaseApp extends ZIOAppDefault {

  private lazy val analyzeWeatherData: Task[Unit] = ZIO.scoped {
    withReaderAndWriter[Any].flatMap {
      case (weatherData, results) =>
        analyzeWeatherDataFiles(weatherData, results)
    }
  }

  override def run: ZIO[ZIOAppArgs with Scope, Throwable, Unit] = analyzeWeatherData

  private def withReaderAndWriter[R]: ZIO[Scope with R, Throwable, (BufferedReader, BufferedWriter)] = {
    val readerPathname = "src/main/resources/temperatures.txt"
    val writerPathname = "src/main/resources/results.txt"

    val reader = ZIO.acquireRelease(openReader(readerPathname))(closeReader)
    val writer = ZIO.acquireRelease(openWriter(writerPathname))(closeWriter)

    reader <*> writer
  }

}
