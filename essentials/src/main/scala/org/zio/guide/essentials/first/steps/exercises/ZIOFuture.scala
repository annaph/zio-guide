package org.zio.guide.essentials.first.steps.exercises

import org.zio.guide.essentials.first.steps.exercises.Implicits.StringOps
import zio.{Console, Scope, Task, ZIO, ZIOAppArgs, ZIOAppDefault}

import scala.concurrent.{ExecutionContext, Future}

object ZIOFuture {

  import QueryService._

  def doQuery(query: Query): Task[Result] = ZIO.fromFuture { implicit ex: ExecutionContext =>
    doQueryFuture(query)
  }

}

object QueryService {

  private val results = Map(
    "get 1" -> Seq("A", "B", "C"),
    "get 2" -> Seq("X", "Y", "Z"),
    "get 3" -> Seq("W")
  )

  def doQueryFuture(query: Query)(implicit ex: ExecutionContext): Future[Result] = Future {
    Result(value =
      results.getOrElse(key = query.sql, default = Seq.empty[String])
    )
  }

  case class Query(sql: String)

  case class Result(value: Seq[String])

}

object QueryServiceApp extends ZIOAppDefault {

  import QueryService.Query
  import ZIOFuture.doQuery

  override def run: ZIO[Any with ZIOAppArgs with Scope, Throwable, Unit] =
    doQuery(query = Query(sql = "get 1"))
      .map(_.value.mkString(","))
      .map(_.withGreenBackground)
      .flatMap(Console.printLine(_))

}
