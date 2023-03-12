package org.zio.guide.essentials.exercises

import org.zio.guide.essentials.exercises.Implicits.StringOps
import zio.{Console, Scope, Task, ZIO, ZIOAppArgs, ZIOAppDefault}

import scala.util.{Failure, Success, Try}

object ZIOAsync {

  import CountryCache.cacheValueAsync
  import UserService.{User, saveUserAsync}

  def getCacheValue(key: Int): Task[String] = ZIO.async { callback =>
    cacheValueAsync(key)(
      onSuccess = value => callback(ZIO.succeed(value)),
      onFailure = ex => callback(ZIO.fail(ex))
    )
  }

  def saveUser(user: User): Task[Unit] = ZIO.async { callback =>
    saveUserAsync(user)(
      onSuccess = () => callback(
        Console.printLine(line = s"$user saved".withGreenBackground)
          .flatMap(_ => ZIO.succeed(()))
      ),
      onFailure = ex => callback(ZIO.fail(ex))
    )
  }

}

object CountryCache {

  private val cache = Map(
    1 -> "EU",
    2 -> "US",
    3 -> "UA"
  )

  def cacheValueAsync(id: Int)(onSuccess: String => Unit, onFailure: Throwable => Unit): Unit =
  // Assume this runs asynchronously
    Try(cache(id)) match {
      case Success(country) =>
        onSuccess(country)
      case Failure(ex) =>
        onFailure(ex)
    }

}

object UserService {
  def saveUserAsync(user: User)(onSuccess: () => Unit, onFailure: Throwable => Unit): Unit =
  // Assume this runs asynchronously
    Try(()) match {
      case Success(_) =>
        onSuccess()
      case Failure(ex) =>
        onFailure(ex)
    }

  case class User(name: String, age: Int)

}

object CountryCacheApp extends ZIOAppDefault {

  import ZIOAsync.getCacheValue

  override def run: ZIO[Any with ZIOAppArgs with Scope, Throwable, Unit] =
    getCacheValue(key = 3)
      .map(_.withGreenBackground)
      .flatMap(Console.printLine(_))

}

object UserServiceApp extends ZIOAppDefault {

  import UserService.User
  import ZIOAsync.saveUser

  override def run: ZIO[Any with ZIOAppArgs with Scope, Throwable, Unit] = saveUser(
    user = User(name = "Anna", age = 31)
  )

}
