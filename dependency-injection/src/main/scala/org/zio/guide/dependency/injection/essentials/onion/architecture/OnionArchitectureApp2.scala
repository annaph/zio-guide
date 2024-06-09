package org.zio.guide.dependency.injection.essentials.onion.architecture

import org.zio.guide.dependency.injection.essentials.onion.architecture.BusinessLogic.BusinessLogicLive
import org.zio.guide.dependency.injection.essentials.onion.architecture.Github.GithubLive
import org.zio.guide.dependency.injection.essentials.onion.architecture.Http.HttpLive
import org.zio.guide.dependency.injection.essentials.onion.architecture.HttpConfig.HttpConfigLive
import org.zio.guide.dependency.injection.essentials.onion.architecture.Implicits.StringOps
import zio.{Console, Scope, Task, ZIO, ZIOAppArgs, ZIOAppDefault}

object OnionArchitectureApp2 extends ZIOAppDefault {

  override def run: ZIO[ZIOAppArgs with Scope, Throwable, Unit] =
    for {
      _ <- Console.printLine(line = s"Starting BusinessLogic...".withGreenBackground).orDie
      _ <- app
      _ <- Console.printLine(line = s"Finished BusinessLogic :)".withGreenBackground).orDie
    } yield ()

  private def app: Task[Unit] =
    ZIO.serviceWithZIO[BusinessLogic](_.runDemo)
      .provide(
        BusinessLogicLive.layer,
        GithubLive.layer,
        HttpLive.layer,
        HttpConfigLive.layer
        /*
        Display a tree visualization of the constructed dependency graph:
          zio.ZLayer.Debug.tree // or zio.ZLayer.Debug.mermaid
        Note: will generate a compilation error!
         */
      )

}
