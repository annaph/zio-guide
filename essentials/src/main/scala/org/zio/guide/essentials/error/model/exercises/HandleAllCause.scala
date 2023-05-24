package org.zio.guide.essentials.error.model.exercises

import org.zio.guide.essentials.first.steps.exercises.Implicits.StringOps
import zio.{Cause, Console, Scope, Task, UIO, ZIO, ZIOAppArgs, ZIOAppDefault}

object HandleAllCause {

  def catchAllCause[R, E1, E2 >: E1, A](effect: ZIO[R, E1, A], handler: Cause[E1] => ZIO[R, E2, A]): ZIO[R, E2, A] =
    effect
      .sandbox
      .catchAll(handler)

  def catchAllCause2[R, E1, E2 >: E1, A](effect: ZIO[R, E1, A], handler: Cause[E1] => ZIO[R, E2, A]): ZIO[R, E2, A] =
    effect.foldCauseZIO(
      failure = handler,
      success = ZIO.succeed(_)
    )

}

object DivideApp extends ZIOAppDefault {

  private val goodEffect: UIO[Float] = ZIO.succeed((3 / 2).toFloat)

  private val badEffect: UIO[Float] = ZIO.succeed((3 / 0).toFloat)

  private val handler: Cause[Throwable] => Task[Float] = {
    case Cause.Die(_: ArithmeticException, _) =>
      ZIO.succeed(0f)
    case cause =>
      ZIO.refailCause(cause)
  }

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] = {
    val good = HandleAllCause.catchAllCause(goodEffect, handler)
    val bad = HandleAllCause.catchAllCause(badEffect, handler)

    (good <*> bad).flatMap {
      case (left, right) =>
        val line =
          s"""
             |${s"good: '$left'".withGreenBackground}
             |${s"bad: '$right'".withRedBackground}
             |""".stripMargin

        Console.print(line)
    }
  }

}

object Divide2App extends ZIOAppDefault {

  private val goodEffect: UIO[Float] = ZIO.succeed((3 / 2).toFloat)

  private val badEffect: UIO[Float] = ZIO.succeed((3 / 0).toFloat)

  private val handler: Cause[Throwable] => Task[Float] = {
    case Cause.Die(_: ArithmeticException, _) =>
      ZIO.succeed(0f)
    case cause =>
      ZIO.refailCause(cause)
  }

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] = {
    val good = HandleAllCause.catchAllCause2(goodEffect, handler)
    val bad = HandleAllCause.catchAllCause2(badEffect, handler)

    (good <*> bad).flatMap {
      case (left, right) =>
        val line =
          s"""
             |${s"good: '$left'".withGreenBackground}
             |${s"bad: '$right'".withRedBackground}
             |""".stripMargin

        Console.print(line)
    }
  }

}
