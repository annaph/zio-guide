package org.zio.guide.resource.handling

import org.zio.guide.resource.handling.{analyzeF1TeamsData => analyzeF1TeamsDataFile}
import zio.{Scope, Task, ZIO, ZIOAppArgs, ZIOAppDefault}

import java.io.BufferedReader

object MyMemoizedResourcefulFunctionApp extends ZIOAppDefault {

  private lazy val analyzeF1TeamsData: Task[Unit] =
    MyZIOOps.scoped {
      for {
        memoizedResourcefulFunction <- MyZIOOps.memoize(f1TeamsData)
        f1TeamsReader1 <- memoizedResourcefulFunction(1).flatMap(_.await)
        _ <- analyzeF1TeamsDataFile(f1TeamsReader1, take = 3)
        f1TeamsReader2 <- memoizedResourcefulFunction(1).flatMap(_.await)
        _ <- analyzeF1TeamsDataFile(f1TeamsReader2, take = 3)
        f1TeamsReader3 <- memoizedResourcefulFunction(1).flatMap(_.await)
        _ <- analyzeF1TeamsDataFile(f1TeamsReader3, take = 3)
      } yield ()
    }

  override def run: ZIO[Any with ZIOAppArgs with Scope, Throwable, Unit] = analyzeF1TeamsData

  private def f1TeamsData[R]: Int => ZIO[R with Scope, Throwable, BufferedReader] =
    code => {
      val f1Pathname = "src/main/resources/f1_teams.txt"

      if (code == 1) MyZIOOps.acquireRelease(openReader(f1Pathname))(closeReader)
      else ZIO.fail(new Exception(s"Not supported F1 code: $code!"))
    }

}
