package org.zio.guide.essentials.error.model.exercises

import org.zio.guide.essentials.first.steps.exercises.Implicits.StringOps
import zio.{Console, Exit, IO, Scope, ZIO, ZIOAppArgs, ZIOAppDefault}

import java.io.IOException

object OnAnyFailure {

  def onAnyFailure[R, E, A](effect: ZIO[R, E, A], handler: ZIO[R, E, Any]): ZIO[R, E, A] =
    effect.foldCauseZIO(
      failure = cause => {
        handler *> ZIO
          .succeed(Exit.Failure(cause))
          .unexit
      },
      success = ZIO.succeed(_)
    )

}

object LogOnErrorApp extends ZIOAppDefault {

  private val effect: IO[Exception, String] = ZIO.fail(new Exception("Error occurred!"))

  private val handler: IO[IOException, Unit] = Console.printLine(line = "==> Logging error event".withMagentaBackground)

  override def run: ZIO[Any with ZIOAppArgs with Scope, Exception, String] =
    OnAnyFailure.onAnyFailure(effect, handler)

}
