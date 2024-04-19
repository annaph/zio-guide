package org.zio.guide.resource.handling

import org.zio.guide.resource.handling.Implicits.StringOps
import zio.{Console, Scope, Task, ZIO, ZIOAppArgs, ZIOAppDefault}

import java.io.BufferedReader
import scala.annotation.tailrec

object MultipleFinalizersApp extends ZIOAppDefault {

  private lazy val mlsTeamsFile: ZIO[Scope, Throwable, BufferedReader] =
    openReader(pathname = "src/main/resources/mls_teams.txt")
      .withFinalizer(closeReader)
      .withFinalizer(_ => Console.printLine(line = s"Finalizer 1!".withBlueBackground).orDie)
      .withFinalizer(_ => Console.printLine(line = s"Finalizer 2!".withBlueBackground).orDie)
      .parallelFinalizers

  private lazy val analyzeMlsTeamsData: Task[Unit] =
    ZIO.scoped {
      for {
        mlsTeamsReader <- mlsTeamsFile
        numOfTeams <- analyze(mlsTeamsReader)
        _ <- Console.printLine(line = s"~> Number of MLS football teams: $numOfTeams".withGreenBackground).orDie
      } yield ()
    }

  override def run: ZIO[ZIOAppArgs with Scope, Throwable, Unit] = analyzeMlsTeamsData

  private def analyze(mlsTeamsReader: BufferedReader): Task[Int] =
    ZIO.attempt {
      @tailrec
      def go(acc: Int = 0): Int = {
        Option(mlsTeamsReader.readLine()) match {
          case Some(_) => go(acc + 1)
          case None => acc
        }
      }

      go()
    }

}
