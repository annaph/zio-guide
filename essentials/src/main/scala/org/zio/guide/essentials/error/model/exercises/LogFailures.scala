package org.zio.guide.essentials.error.model.exercises

import org.zio.guide.essentials.first.steps.exercises.Implicits.StringOps
import zio.Cause.Fail
import zio.{Cause, Console, Scope, Task, UIO, ZIO, ZIOAppArgs, ZIOAppDefault}

import java.io.IOException

object LogFailures {

  def logFailures[R, E, A](effect: ZIO[R, E, A]): ZIO[R, E, A] =
    effect.foldCauseZIO(
      failure = handleCause,
      success = ZIO.succeed(_)
    )

  private def handleCause[R, E, A](cause: Cause[E]): ZIO[R, E, A] = {
    val causeStr =
      s"""
         |Cause ==>
         |${cause.prettyPrint}
         |<==
         |""".stripMargin

    println(causeStr.withRedBackground)

    cause match {
      case Fail(error, _) =>
        ZIO.fail(error)
      case _ =>
        ZIO.die(new Throwable("Cannot recover from defect!"))
    }
  }

}

object LogErrorApp extends ZIOAppDefault {

  private val effect: Task[String] = ZIO.fail(new Throwable("Error occurred!"))

  override def run: ZIO[Any with ZIOAppArgs with Scope, IOException, Unit] =
    LogFailures
      .logFailures(effect)
      .fold(
        failure = _ => "Original effect failed with error!".withMagentaBackground,
        success = _ => "Original effect succeeded".withGreenBackground
      )
      .flatMap(Console.printLine(_))

}

object LogDefectApp extends ZIOAppDefault {

  private val effect: UIO[String] = ZIO.succeed(throw new Exception("Defect occurred!"))

  override def run: ZIO[Any with ZIOAppArgs with Scope, IOException, Unit] =
    LogFailures
      .logFailures(effect)
      .map(_ => "This should never happen!".withRedBackground)
      .flatMap(Console.printLine(_))

}
