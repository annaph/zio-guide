package org.zio.guide.resource.handling

import zio.{Exit, Promise, Ref, Scope, UIO, ZEnvironment, ZIO}

object MyZIOOps {

  def acquireRelease[R, R1, E, A](acquire: => ZIO[R, E, A])
                                 (release: A => ZIO[R1, Nothing, Any]): ZIO[R with R1 with Scope, E, A] =
    ZIO.uninterruptible {
      for {
        releaseEnv <- ZIO.environment[R1]
        scope <- ZIO.scope
        resource <- acquire
        _ <- scope.addFinalizer(release(resource).provideEnvironment(releaseEnv))
      } yield resource
    }

  def scoped[R, E, A](zio: => ZIO[Scope with R, E, A]): ZIO[R, E, A] =
    for {
      scope <- Scope.make
      result <- scope.use[R](zio)
    } yield result

  def withEarlyRelease[R, E, A](zio: ZIO[R with Scope, E, A]): ZIO[R with Scope, E, (UIO[Unit], A)] =
    for {
      scope <- ZIO.service[Scope]
      childScope <- scope.fork
      a <- zio.provideSomeEnvironment[R](_.union[Scope](ZEnvironment(childScope)))
    } yield childScope.close(Exit.unit) -> a

  def memoize[R, E, A, B](f: A => ZIO[R with Scope, E, B]): ZIO[R with Scope, Nothing, A => ZIO[R with Scope, Nothing, Promise[E, B]]] =
    for {
      scope <- ZIO.service[Scope]
      ref <- Ref.Synchronized.make(Map.empty[A, Promise[E, B]])
    } yield (a: A) => {
      Promise
        .make[E, B]
        .flatMap { newResource =>
          ref.modifyZIO { cache =>
            cache.get(a) match {
              case Some(resource) => ZIO.succeed(resource -> cache)
              case None =>
                scope.extend[R](f(a)).intoPromise(newResource) *>
                  ZIO.succeed(newResource -> (cache + (a -> newResource)))
            }
          }
        }
    }

}
