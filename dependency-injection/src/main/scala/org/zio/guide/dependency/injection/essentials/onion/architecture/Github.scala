package org.zio.guide.dependency.injection.essentials.onion.architecture

import org.zio.guide.dependency.injection.essentials.onion.architecture.Github.GithubLive.{printGetResponse, printPostResponse}
import org.zio.guide.dependency.injection.essentials.onion.architecture.Implicits.StringOps
import org.zio.guide.dependency.injection.essentials.onion.architecture.Model.{Comment, Issue}
import zio.{Chunk, Console, Task, UIO, ZLayer}

import java.nio.charset.StandardCharsets

trait Github {
  def issues(organization: String): Task[Chunk[Issue]]

  def postComment(issue: Issue, comment: Comment): Task[Unit]
}

object Github {

  final case class GithubLive(http: Http) extends Github {

    override def issues(organization: String): Task[Chunk[Issue]] =
      http
        .get(url = s"api/issue/list?organization=$organization")
        .map(Issue.fromChunks)
        .tap(printGetResponse)

    override def postComment(issue: Issue, comment: Comment): Task[Unit] =
      http
        .post(url = "api/comment/save", body = comment.toChunks)
        .tap(printPostResponse)
        .map(_ => ())

  }

  object GithubLive {

    val layer: ZLayer[Http, Nothing, Github] =
      ZLayer.fromFunction[Http => Github](GithubLive(_))

    private def printGetResponse(issues: Chunk[Issue]): UIO[Unit] =
      Console
        .print(line = s"~> Got GET response: ${issues.mkString("", " | ", "\n")}".withBlueBackground)
        .orDie

    private def printPostResponse(response: Chunk[Byte]): UIO[Unit] =
      Console
        .printLine(
          line = s"~> Got POST response: ${new String(response.toArray, StandardCharsets.UTF_8)}".withBlueBackground
        )
        .orDie

  }

}
