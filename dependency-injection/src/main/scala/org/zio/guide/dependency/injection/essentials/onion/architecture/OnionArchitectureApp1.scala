package org.zio.guide.dependency.injection.essentials.onion.architecture

import org.zio.guide.dependency.injection.essentials.onion.architecture.BusinessLogic.BusinessLogicLive
import org.zio.guide.dependency.injection.essentials.onion.architecture.Github.GithubLive
import org.zio.guide.dependency.injection.essentials.onion.architecture.Http.HttpLive
import org.zio.guide.dependency.injection.essentials.onion.architecture.HttpConfig.HttpConfigLive
import org.zio.guide.dependency.injection.essentials.onion.architecture.Implicits.{StringOps, twoSeconds}
import zio.{Console, Scope, ZIO, ZIOAppArgs, ZIOAppDefault}

object OnionArchitectureApp1 extends ZIOAppDefault {

  private val httpConfig: HttpConfig = HttpConfigLive(twoSeconds)
  private val http: Http = HttpLive(httpConfig)
  private val github: Github = GithubLive(http)
  private val businessLogic: BusinessLogic = BusinessLogicLive(github)

  override def run: ZIO[ZIOAppArgs with Scope, Throwable, Unit] =
    for {
      _ <- Console.printLine(line = s"Starting BusinessLogic...".withGreenBackground).orDie
      _ <- businessLogic.runDemo
      _ <- Console.printLine(line = s"Finished BusinessLogic :)".withGreenBackground).orDie
    } yield ()

}
