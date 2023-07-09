package org.zio.guide.essentials.integration

import cats.data.Kleisli
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.dsl.Http4sDsl
import org.http4s.implicits._
import org.http4s.server.{Router, Server}
import org.http4s.{HttpRoutes, Request, Response}
import org.log4s.getLogger
import zio.interop.catz._
import zio.interop.catz.implicits._
import zio.{Scope, Task, UIO, ZIO, ZIOAppArgs, ZIOAppDefault}

object Http4sApp extends ZIOAppDefault {

  private val httpApp: Kleisli[Task, Request[Task], Response[Task]] =
    Router("/" -> HelloWorldEndpoint.helloWorldRoute).orNotFound

  private val server: ZIO[Scope, Throwable, Server] =
    ZIO.executor
      .map(_.asExecutionContext)
      .flatMap { exCtx =>
        BlazeServerBuilder[Task]
          .withExecutionContext(exCtx)
          .bindHttp(port = 8080, host = "localhost")
          .withHttpApp(httpApp)
          .resource
          .toScopedZIO
      }

  private val useServer: Task[Nothing] = ZIO.scoped {
    server *> ZIO.never
  }

  override def run: ZIO[Any with ZIOAppArgs with Scope, Throwable, Nothing] =
    useServer

}

object HelloWorldEndpoint {

  private[this] val logger = getLogger

  private val htt4sDsl = Http4sDsl[Task]

  import htt4sDsl._

  val helloWorldRoute: HttpRoutes[Task] = HttpRoutes.of[Task] {
    case GET -> Root / "hello" / name =>
      logger.debug("hello endpoint invoked :)")
      Ok {
        HelloWorldService.sayHello(name)
      }
  }

}

object HelloWorldService {

  def sayHello(name: String): UIO[String] = ZIO.succeed {
    s"Hello, $name :)"
  }

}
