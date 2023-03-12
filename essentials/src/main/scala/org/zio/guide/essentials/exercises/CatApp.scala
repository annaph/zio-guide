package org.zio.guide.essentials.exercises

import org.zio.guide.essentials.exercises.FileOps.readFile
import org.zio.guide.essentials.exercises.Implicits.StringOps
import zio.{Console, Scope, ZIO, ZIOAppArgs, ZIOAppDefault}

object CatApp extends ZIOAppDefault {

  private val inputFiles: ZIO[ZIOAppArgs, Nothing, List[String]] =
    this.getArgs.map(_.toList)

  override def run: ZIO[Any with ZIOAppArgs with Scope, Throwable, Unit] =
    inputFiles
      .flatMap(ZIO.foreach(_)(readFile))
      .flatMap { contents =>
        Console.print(contents.mkString("\n\n").withGreenBackground)
      }

}
