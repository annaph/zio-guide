package org.zio.guide.essentials.error.model.exercises

import org.zio.guide.essentials.first.steps.exercises.Implicits.StringOps
import zio.{Console, Scope, UIO, ZIO, ZIOAppArgs, ZIOAppDefault}

import java.io.IOException

object RecoverFromDefects {

  def recoverFromSomeDefects[R, E, A](effect: ZIO[R, E, A])(f: Throwable => Option[A]): ZIO[R, E, A] = {
    effect.foldCauseZIO(
      failure = _.defects
        .map(f)
        .find(_.isDefined)
        .flatten
        .map(ZIO.succeed(_))
        .getOrElse(ZIO.die(new Throwable("Cannot recover from defect!"))),
      success = ZIO.succeed(_)
    )
  }

}

object RecoverFromDefectApp extends ZIOAppDefault {

  private val effect: UIO[String] = ZIO.succeed(throw new RuntimeException())

  private val recover: Throwable => Option[String] = {
    case _: RuntimeException =>
      Some("Recovered from RuntimeException :)")
    case _ =>
      None
  }

  override def run: ZIO[Any with ZIOAppArgs with Scope, IOException, Unit] =
    RecoverFromDefects.recoverFromSomeDefects(effect)(recover)
      .flatMap { contents =>
        Console.printLine(contents.withGreenBackground)
      }

}

object DoNotRecoverFromDefectApp extends ZIOAppDefault {

  private val effect: UIO[String] = ZIO.succeed(throw new RuntimeException())

  private val recover: Throwable => Option[Nothing] = { _ =>
    Option.empty[Nothing]
  }

  override def run: ZIO[Any with ZIOAppArgs with Scope, Nothing, String] =
    RecoverFromDefects.recoverFromSomeDefects(effect)(recover)

}
