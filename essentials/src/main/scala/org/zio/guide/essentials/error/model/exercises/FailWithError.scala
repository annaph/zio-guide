package org.zio.guide.essentials.error.model.exercises

import zio.{IO, Scope, ZIO, ZIOAppArgs, ZIOAppDefault}

object FailWithError {

  def failWithMessage(str: String): IO[Error, String] =
    ZIO.fail(new Error(str))

}

object FailWithErrorApp extends ZIOAppDefault {

  override def run: ZIO[Any with ZIOAppArgs with Scope, Error, String] =
    FailWithError.failWithMessage(str = "Error, Anna!")

}
