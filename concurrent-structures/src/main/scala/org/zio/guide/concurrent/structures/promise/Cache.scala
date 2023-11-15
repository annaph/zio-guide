package org.zio.guide.concurrent.structures.promise

import zio.{IO, Promise, Ref, URIO, ZIO}

sealed trait Cache[-K, +E, +V] {
  def get(key: K): IO[E, V]
}

object Cache {
  def make[K, R, E, V](lookup: K => ZIO[R, E, V]): URIO[R, Cache[K, E, V]] =
    for {
      env <- ZIO.environment[R]
      ref <- Ref.make[Map[K, Promise[E, V]]](Map.empty)
    } yield new Cache[K, E, V] {
      override def get(key: K): IO[E, V] = {
        Promise.make[E, V]
          .flatMap { newPromise =>
            ref.modify { map =>
              map.get(key) match {
                case Some(oldPromise) =>
                  Right(oldPromise) -> map
                case None =>
                  Left(newPromise) -> (map + (key -> newPromise))
              }
            }
          }.flatMap {
            case Left(newPromise) =>
              lookup(key)
                .provideEnvironment(env)
                .intoPromise(newPromise) *> newPromise.await
            case Right(oldPromise) =>
              oldPromise.await
          }
      }
    }
}
