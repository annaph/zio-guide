package org.zio.guide.resource.handling

import zio.{Scope, Task, ZIO, ZIOAppArgs, ZIOAppDefault}

import java.io.{BufferedReader, BufferedWriter}

object ScopeApp extends ZIOAppDefault {

  private lazy val analyzeWeatherData: Task[Unit] = ZIO.scoped {
    withReaderAndWriter[Any].flatMap {
      case (weatherData, results) =>
        analyze(weatherData, results)
    }
  }

  private val readerPathname = "src/main/resources/temperatures.txt"
  private val writerPathname = "src/main/resources/results.txt"

  override def run: ZIO[ZIOAppArgs with Scope, Throwable, Unit] = analyzeWeatherData

  private def withReaderAndWriter[R]: ZIO[Scope with R, Throwable, (BufferedReader, BufferedWriter)] = {
    val reader = ZIO.acquireRelease(openReader(readerPathname))(closeReader)
    val writer = ZIO.acquireRelease(openWriter(writerPathname))(closeWriter)

    reader <*> writer
  }

}
