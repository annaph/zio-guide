package org.zio.guide.essentials.error.model.exercises

import org.zio.guide.essentials.first.steps.exercises.Implicits.StringOps
import zio.{Console, IO, Scope, Task, ZIO, ZIOAppArgs, ZIOAppDefault}

import java.io.IOException

object RefineOrDie {

  def ioException[R, A](effect: ZIO[R, Throwable, A]): ZIO[R, IOException, A] =
    effect.refineOrDie {
      case ex: IOException =>
        ex
    }

  def parseNumber(number: String): IO[NumberFormatException, Int] =
    ZIO.attempt(number.toInt).refineOrDie {
      case ex: NumberFormatException =>
        ex
    }

}

object RefineThrowableApp extends ZIOAppDefault {

  private val effect: Task[String] = ZIO.fail(new IOException("I/O error occurred!"))

  override def run: ZIO[Any with ZIOAppArgs with Scope, IOException, Unit] =
    RefineOrDie
      .ioException(effect)
      .fold(
        failure = _ => "Recovered from I/O error".withMagentaBackground,
        success = _ => "Original effect succeeded".withGreenBackground
      )
      .flatMap(Console.printLine(_))

}

object DieApp extends ZIOAppDefault {

  private val effect: Task[String] = ZIO.fail {
    new UnsupportedOperationException("Unsupported operation error occurred!")
  }

  override def run: ZIO[Any with ZIOAppArgs with Scope, IOException, Unit] =
    RefineOrDie
      .ioException(effect)
      .fold(
        failure = _ => "This should never happen!".withRedBackground,
        success = _ => "Original effect succeeded".withGreenBackground
      )
      .flatMap(Console.printLine(_))

}

object ParseNumberErrorApp extends ZIOAppDefault {

  private val invalidNumber = "invalid-number"

  override def run: ZIO[Any with ZIOAppArgs with Scope, IOException, Unit] =
    RefineOrDie
      .parseNumber(invalidNumber)
      .fold(
        failure = _ => s"Unable to parse number: '$invalidNumber'!".withMagentaBackground,
        success = _ => "This should never happen!".withRedBackground
      )
      .flatMap(Console.printLine(_))

}
