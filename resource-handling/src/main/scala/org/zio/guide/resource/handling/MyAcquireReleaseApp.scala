package org.zio.guide.resource.handling

import org.zio.guide.resource.handling.{analyzeF1TeamsData => analyzeF1TeamsDataFile}
import zio.{Scope, Task, ZIO, ZIOAppArgs, ZIOAppDefault}

import java.io.BufferedReader

object MyAcquireReleaseApp extends ZIOAppDefault {

  private lazy val f1TeamsData: ZIO[Scope, Throwable, BufferedReader] = {
    val f1Pathname = "src/main/resources/f1_teams.txt"
    MyZIOOps.acquireRelease(openReader(f1Pathname))(closeReader)
  }

  private lazy val analyzeF1TeamsData: Task[Unit] =
    MyZIOOps.scoped {
      for {
        f1TeamsReader <- f1TeamsData
        _ <- analyzeF1TeamsDataFile(f1TeamsReader)
      } yield ()
    }

  override def run: ZIO[Any with ZIOAppArgs with Scope, Throwable, Unit] = analyzeF1TeamsData

}
