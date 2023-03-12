package org.zio.guide.essentials

import zio.{Scope, Task, ZIO, ZIOAppArgs, ZIOAppDefault}

import scala.io.StdIn

object BuildFullName extends ZIOAppDefault {

  private val firstName: Task[String] = ZIO.attempt {
    println("Enter first name: ")
    StdIn.readLine()
  }

  private val lastName: Task[String] = ZIO.attempt {
    println("Enter last name: ")
    StdIn.readLine()
  }

  private val fullName: Task[String] = firstName.zipWith(lastName) {
    case (first, last) =>
      s"$first $last"
  }

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Unit] =
    fullName.flatMap(printLine)

}
