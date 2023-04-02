package org.zio.guide.essentials.first.steps.exercises

import org.zio.guide.essentials.first.steps.exercises.Implicits.StringOps
import zio.{Console, Scope, UIO, ZIO, ZIOAppArgs, ZIOAppDefault}

import java.io.IOException

object ZIOSucceedAndFail {

  lazy val currentTime: UIO[Long] = ZIO.succeed {
    java.lang.System.currentTimeMillis()
  }

  def eitherToZIO[E, A](either: Either[E, A]): ZIO[Any, E, A] = either match {
    case Left(e) =>
      ZIO.fail(e)
    case Right(value) =>
      ZIO.succeed(value)
  }

  def headListToZIO[A](list: List[A]): ZIO[Any, None.type, A] = list match {
    case Nil =>
      ZIO.fail(None)
    case x :: _ =>
      ZIO.succeed(x)
  }

}

object RightApp extends ZIOAppDefault {

  import ZIOSucceedAndFail.eitherToZIO

  private val right = Right("I'm right!")

  override def run: ZIO[Any with ZIOAppArgs with Scope, IOException, Unit] =
    eitherToZIO(right)
      .map(_.withGreenBackground)
      .flatMap(Console.printLine(_))

}

object LeftApp extends ZIOAppDefault {

  import ZIOSucceedAndFail.eitherToZIO

  private val left = Left("I'm left!".withRedBackground)

  override def run: ZIO[Any with ZIOAppArgs with Scope, String, String] =
    eitherToZIO[String, String](left)

}

object ListHeadApp extends ZIOAppDefault {

  import ZIOSucceedAndFail.headListToZIO

  private val countries = List("EU", "US", "UA")

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Unit] =
    headListToZIO(countries)
      .map(_.withGreenBackground)
      .flatMap(Console.printLine(_))

}

object ListEmptyApp extends ZIOAppDefault {

  import ZIOSucceedAndFail.headListToZIO

  private val countries = List.empty[String]

  override def run: ZIO[Any with ZIOAppArgs with Scope, None.type, String] =
    headListToZIO(countries)

}

object CurrentTimeApp extends ZIOAppDefault {

  import ZIOSucceedAndFail.currentTime

  override def run: ZIO[Any with ZIOAppArgs with Scope, IOException, Unit] =
    currentTime
      .map(_.toString.withGreenBackground)
      .flatMap(Console.printLine(_))

}
