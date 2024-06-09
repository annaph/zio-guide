package org.zio.guide.dependency.injection.essentials.onion.architecture

import org.zio.guide.dependency.injection.essentials.onion.architecture.Model.Comment
import zio.{Task, ZIO, ZLayer}

trait BusinessLogic {
  def runDemo: Task[Unit]
}

object BusinessLogic {

  final case class BusinessLogicLive(github: Github) extends BusinessLogic {
    override def runDemo: Task[Unit] =
      for {
        issues <- github.issues(organization = "Home")
        comment = Comment(text = "Some comment")
        _ <- ZIO
          .getOrFail(issues.headOption)
          .flatMap(github.postComment(_, comment))
      } yield ()
  }

  object BusinessLogicLive {
    val layer: ZLayer[Github, Nothing, BusinessLogic] =
      ZLayer.fromFunction[Github => BusinessLogic](BusinessLogicLive(_))
  }

}
