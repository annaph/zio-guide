package org.zio.guide.resource.handling

import org.zio.guide.resource.handling.Implicits.StringOps
import zio.{Console, Scope, UIO, URIO, ZIO, ZIOAppArgs, ZIOAppDefault}

object MyWithEarlyReleaseApp extends ZIOAppDefault {

  private lazy val workWithResources: UIO[Unit] =
    ZIO.scoped {
      for {
        _ <- resource(label = "A")
        resourceWithEarlyRelease <- MyZIOOps.withEarlyRelease(resource(label = "B"))
        (earlyRelease, _) = resourceWithEarlyRelease
        _ <- earlyRelease
        _ <- Console.printLine(line = "Using A".withGreenBackground).orDie
      } yield ()
    }

  override def run: ZIO[ZIOAppArgs with Scope, Nothing, Unit] = workWithResources

  private def resource(label: String): URIO[Scope, Unit] = {
    val acquire = Console
      .printLine(line = s"Acquiring $label".withBlueBackground)
      .orDie

    val release = Console
      .printLine(line = s"Releasing $label".withBlueBackground)
      .orDie

    MyZIOOps.acquireRelease(acquire)(_ => release)
  }

}
