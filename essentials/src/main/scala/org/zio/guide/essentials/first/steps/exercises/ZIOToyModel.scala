package org.zio.guide.essentials.first.steps.exercises

import org.zio.guide.essentials.first.steps.exercises.Implicits.StringOps

import scala.annotation.tailrec
import scala.collection.mutable
import scala.util.Try

object ZIOToyModel {

  def zipWith[R, E, A, B, C](self: ZIO[R, E, A], that: ZIO[R, E, B])(f: (A, B) => C): ZIO[R, E, C] =
    ZIO { env: R =>
      for {
        a <- self.run(env)
        b <- that.run(env)
      } yield f(a, b)
    }

  def foreach[R, E, A, B](in: Iterable[A])(f: A => ZIO[R, E, B]): ZIO[R, E, List[B]] =
    collectAll(in.map(f))

  def collectAll[R, E, A](in: Iterable[ZIO[R, E, A]]): ZIO[R, E, List[A]] =
    ZIO { env: R =>
      @tailrec
      def go(effects: List[ZIO[R, E, A]],
             result: mutable.ListBuffer[A] = mutable.ListBuffer.empty[A]): Either[E, mutable.ListBuffer[A]] =
        effects match {
          case Nil =>
            Right(result)

          case effect :: rest =>
            effect.run(env) match {
              case Left(e) =>
                Left(e)
              case Right(a) =>
                go(rest, result :+ a)
            }
        }

      go(in.toList, mutable.ListBuffer.empty[A]).map(_.toList)
    }

  def orElse[R, E1, E2, A](self: ZIO[R, E1, A], fallback: ZIO[R, E2, A]): ZIO[R, E2, A] =
    ZIO { env: R =>
      self.run(env) match {
        case Right(value) =>
          Right(value)
        case Left(_) =>
          fallback.run(env)
      }
    }

  final case class ZIO[-R, +E, +A](run: R => Either[E, A])

}

object ZipWithApp extends App {

  import ZIOToyModel._

  private val a = ZIO[Any, Nothing, Int](run = _ => Right(1))
  private val b = ZIO[Any, Nothing, String](run = _ => Right("EU"))

  private val c = zipWith(a, b) {
    case (x, y) =>
      s"$x - $y"
  }

  private val result = c.run(())

  println(result.toString.withGreenBackground)

}

object CollectAllApp extends App {

  import ZIOToyModel._

  private val effect1 = ZIO[Any, Nothing, String](_ => Right("EU"))
  private val effect2 = ZIO[Any, Nothing, String](_ => Right("US"))
  private val effect3 = ZIO[Any, Nothing, String](_ => Right("UA"))

  private val result = collectAll(Seq(effect1, effect2, effect3)).run(())

  println(result.toString.withGreenBackground)

}

object ForeachApp extends App {

  import ZIOToyModel._

  private val countries = Seq("EU", "US", "UA")

  private val result = foreach(countries) { country =>
    ZIO[Any, Nothing, String](_ => Right(s"${country.toLowerCase}"))
  }.run(())

  println(result.toString.withGreenBackground)

}

object OrElseApp extends App {

  import ZIOToyModel._

  private val result1 = orElse(
    effect(str = "1"),
    fallback = ZIO[Any, Nothing, Int](_ => Right(0))
  ).run(())

  private val result2 = orElse(
    effect(str = "aaa"),
    fallback = ZIO[Any, Nothing, Int](_ => Right(0))
  ).run(())

  private def effect(str: String): ZIO[Any, Throwable, Int] = ZIO { _ =>
    Try(str.toInt).toEither
  }

  println(s"Result 1: $result1".withGreenBackground)
  println(s"Result 2: $result2".withRedBackground)

}
