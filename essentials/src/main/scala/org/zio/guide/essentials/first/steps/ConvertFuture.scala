package org.zio.guide.essentials.first.steps

import zio.{Scope, Task, ZIO, ZIOAppArgs, ZIOAppDefault}

import scala.concurrent.{ExecutionContext, Future}

object ConvertFuture extends ZIOAppDefault {

  private val goShoppingZIO: Task[Unit] = ZIO.fromFuture {
    implicit ec => goShoppingFuture
  }

  override def run: ZIO[Any with ZIOAppArgs with Scope, Throwable, Unit] = goShoppingZIO

  private def goShoppingFuture(implicit ec: ExecutionContext): Future[Unit] = Future {
    println("Going to the grocery store")
  }

}
