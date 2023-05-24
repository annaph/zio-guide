package org.zio.guide.essentials.error.model.exercises

import org.zio.guide.essentials.first.steps.exercises.Implicits.StringOps
import zio.{Console, IO, Scope, Task, ZIO, ZIOAppArgs, ZIOAppDefault}

import java.io.IOException

object ShiftEither {

  def left[R, E, A, B](effect: ZIO[R, E, Either[A, B]]): ZIO[R, Either[E, B], A] =
    effect.foldZIO(
      failure = e => ZIO.fail(Left(e)),
      success = {
        case Left(a) =>
          ZIO.succeed(a)
        case Right(b) =>
          ZIO.fail(Right(b))
      }
    )

  def unleft[R, E, A, B](effect: ZIO[R, Either[E, B], A]): ZIO[R, E, Either[A, B]] =
    effect.foldZIO(
      failure = {
        case Left(e) =>
          ZIO.fail(e)
        case Right(b) =>
          ZIO.succeed(Right(b))
      },
      success = a => ZIO.succeed(Left(a))
    )

  def right[R, E, A, B](effect: ZIO[R, E, Either[A, B]]): ZIO[R, Either[E, A], B] =
    effect.foldZIO(
      failure = e => ZIO.fail(Left(e)),
      success = {
        case Left(a) =>
          ZIO.fail(Right(a))
        case Right(b) =>
          ZIO.succeed(b)
      }
    )

  def unright[R, E, A, B](effect: ZIO[R, Either[E, A], B]): ZIO[R, E, Either[A, B]] =
    effect.foldZIO(
      failure = {
        case Left(e) =>
          ZIO.fail(e)
        case Right(a) =>
          ZIO.succeed(Left(a))
      },
      success = b => ZIO.succeed(Right(b))
    )

}

object ShiftLeftApp extends ZIOAppDefault {

  private val effect: Task[Either[Int, String]] = ZIO.succeed(Left(1))

  override def run: ZIO[Any with ZIOAppArgs with Scope, IOException, Unit] =
    ShiftEither
      .left(effect)
      .fold(
        failure = _ => "This should never happen!".withRedBackground,
        success = l => s"left value: $l".withGreenBackground
      )
      .flatMap(x => Console.printLine(x))

}

object ShiftLeftAndFailApp extends ZIOAppDefault {

  private val effect: Task[Either[Int, String]] = ZIO.succeed(Right("A"))

  override def run: ZIO[Any with ZIOAppArgs with Scope, IOException, Unit] =
    ShiftEither
      .left(effect)
      .fold(
        failure = either => s"either value: '$either'!".withMagentaBackground,
        success = _ => "This should never happen!".withRedBackground
      )
      .flatMap(Console.printLine(_))

}

object UnLeftApp extends ZIOAppDefault {

  private val effect: IO[Either[Throwable, String], Int] = ZIO.succeed(1)

  override def run: ZIO[Any with ZIOAppArgs with Scope, IOException, Unit] =
    ShiftEither
      .unleft(effect)
      .fold(
        failure = _ => "This should never happen!".withRedBackground,
        success = {
          case Left(l) =>
            s"left value: $l".withGreenBackground
          case Right(_) =>
            "This should never happen!".withRedBackground
        }
      )
      .flatMap(Console.printLine(_))

}

object UnLeftWithErrorApp extends ZIOAppDefault {

  private val effect: IO[Either[Throwable, String], Int] = ZIO.fail(Right("A"))

  override def run: ZIO[Any with ZIOAppArgs with Scope, IOException, Unit] =
    ShiftEither
      .unleft(effect)
      .fold(
        failure = _ => "This should never happen!".withRedBackground,
        success = {
          case Left(_) =>
            "This should never happen!".withRedBackground
          case Right(r) =>
            s"right value: $r".withGreenBackground
        }
      )
      .flatMap(Console.printLine(_))

}

object ShiftRightApp extends ZIOAppDefault {

  private val effect: Task[Either[Int, String]] = ZIO.succeed(Right("A"))

  override def run: ZIO[Any with ZIOAppArgs with Scope, IOException, Unit] =
    ShiftEither
      .right(effect)
      .fold(
        failure = _ => "This should never happen!".withRedBackground,
        success = r => s"right value: $r".withGreenBackground
      )
      .flatMap(x => Console.printLine(x))

}

object ShiftRightAndFailApp extends ZIOAppDefault {

  private val effect: Task[Either[Int, String]] = ZIO.succeed(Left(1))

  override def run: ZIO[Any with ZIOAppArgs with Scope, IOException, Unit] =
    ShiftEither
      .right(effect)
      .fold(
        failure = either => s"either value: '$either'!".withMagentaBackground,
        success = _ => "This should never happen!".withRedBackground
      )
      .flatMap(Console.printLine(_))

}

object UnRightApp extends ZIOAppDefault {

  private val effect: IO[Either[Throwable, Int], String] = ZIO.succeed("A")

  override def run: ZIO[Any with ZIOAppArgs with Scope, IOException, Unit] =
    ShiftEither
      .unright(effect)
      .fold(
        failure = _ => "This should never happen!".withRedBackground,
        success = {
          case Left(_) =>
            "This should never happen!".withRedBackground
          case Right(r) =>
            s"right value: $r".withGreenBackground
        }
      )
      .flatMap(Console.printLine(_))

}

object UnRightWithErrorApp extends ZIOAppDefault {

  private val effect: IO[Either[Throwable, Int], String] = ZIO.fail(Right(1))

  override def run: ZIO[Any with ZIOAppArgs with Scope, IOException, Unit] =
    ShiftEither
      .unright(effect)
      .fold(
        failure = _ => "This should never happen!".withRedBackground,
        success = {
          case Left(l) =>
            s"left value: $l".withGreenBackground
          case Right(_) =>
            "This should never happen!".withRedBackground
        }
      )
      .flatMap(Console.printLine(_))

}
