package org.zio.guide.dependency.injection.essentials.onion.architecture

import org.zio.guide.dependency.injection.essentials.onion.architecture.Implicits.StringOps
import org.zio.guide.dependency.injection.essentials.onion.architecture.Model.Comment
import zio.{Chunk, Console, Task, UIO, ZIO, ZLayer}

import java.nio.charset.StandardCharsets

trait Http {

  def get(url: String): Task[Chunk[Byte]]

  def post(url: String, body: Chunk[Byte]): Task[Chunk[Byte]]

}

object Http {

  final case class HttpLive(httpConfig: HttpConfig) extends Http {

    override def get(url: String): Task[Chunk[Byte]] =
      url match {
        case "api/issue/list?organization=Home" =>
          val issue1 = "id1|title1".getBytes(StandardCharsets.UTF_8)
          val separator = ";".getBytes(StandardCharsets.UTF_8)
          val issue2 = "id2|tittle2".getBytes(StandardCharsets.UTF_8)

          val bytes = issue1 ++ separator ++ issue2

          for {
            _ <- ZIO sleep httpConfig.responseTime
            result <- ZIO.succeed(Chunk(bytes.toIndexedSeq: _*))
          } yield result

        case _ =>
          ZIO.fail(new UnsupportedOperationException(s"Github GET $url not supported!"))
      }

    override def post(url: String, body: Chunk[Byte]): Task[Chunk[Byte]] =
      (url, Comment.fromChunks(body)) match {
        case ("api/comment/save", Some(comment)) =>
          val bytes = s"Comment with text '${comment.text}' saved.".getBytes(StandardCharsets.UTF_8)

          for {
            _ <- ZIO sleep httpConfig.responseTime
            result <- ZIO.succeed(Chunk(bytes.toIndexedSeq: _*))
          } yield result

        case (_, None) =>
          ZIO.fail(new IllegalArgumentException("Body does not represent Comment!"))

        case _ =>
          ZIO.fail(new UnsupportedOperationException(s"Github POST $url not supported!"))
      }

    def start: Task[Unit] =
      Console
        .printLine(line = s"HttpLive service started.".withCyanBackground)

    def shutdown: UIO[Unit] =
      Console
        .printLine(line = s"HttpLive service shutdown.".withCyanBackground)
        .orDie

  }

  object HttpLive {
    val layer: ZLayer[HttpConfig, Throwable, Http] =
      ZLayer.scoped {
        for {
          httpConfig <- ZIO.service[HttpConfig]
          http <- ZIO.succeed(HttpLive(httpConfig))
          _ <- http.start
          _ <- ZIO.addFinalizer(http.shutdown)
        } yield http
      }
  }

}
