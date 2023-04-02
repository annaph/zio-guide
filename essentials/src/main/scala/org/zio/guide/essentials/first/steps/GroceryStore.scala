package org.zio.guide.essentials.first.steps

import zio.{Scope, Task, ZIO, ZIOAppArgs, ZIOAppDefault}

object GroceryStore extends ZIOAppDefault {

  private val goShopping: Task[Unit] = ZIO.attempt {
    println("Going to the grocery store.")
  }

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] = goShopping

}
