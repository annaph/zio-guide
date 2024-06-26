package org.zio.guide.resource.handling

import org.zio.guide.resource.handling.{analyzeWeatherData => analyzeWeatherDataFiles}
import zio.{Scope, Task, ZIO, ZIOAppArgs, ZIOAppDefault}

import java.io.{BufferedReader, BufferedWriter}

object AcquireReleaseWithApp extends ZIOAppDefault {

  private lazy val analyzeWeatherData: Task[Unit] =
    withReader(pathname = "src/main/resources/temperatures.txt") { weatherData =>
      withWriter(pathname = "src/main/resources/results.txt") { results =>
        analyzeWeatherDataFiles(weatherData, results)
      }
    }

  override def run: ZIO[ZIOAppArgs with Scope, Throwable, Unit] = analyzeWeatherData

  private def withReader[A](pathname: String)(use: BufferedReader => Task[A]): Task[A] =
    ZIO.acquireReleaseWith(openReader(pathname))(closeReader)(use)

  private def withWriter[A](pathname: String)(use: BufferedWriter => Task[A]): Task[A] =
    ZIO.acquireReleaseWith(openWriter(pathname))(closeWriter)(use)

}
