package org.zio.guide.concurrent.structures.semaphore

import zio.{Ref, Semaphore, UIO, ZIO}

trait RefM[A] {

  def get: UIO[A]

  def modify[R, E, B](f: A => ZIO[R, E, (B, A)]): ZIO[R, E, B]

}

object RefM {

  def make[A](a: A): UIO[RefM[A]] =
    for {
      ref <- Ref.make(a)
      semaphore <- Semaphore.make(permits = 1)
    } yield new RefMImpl(ref, semaphore)

  private class RefMImpl[A](ref: Ref[A], semaphore: Semaphore) extends RefM[A] {

    override def get: UIO[A] = ref.get

    override def modify[R, E, B](f: A => ZIO[R, E, (B, A)]): ZIO[R, E, B] =
      semaphore.withPermit {
        for {
          value <- ref.get
          result <- f(value)
          (b, newValue) = result
          _ <- ref.set(newValue)
        } yield b
      }
  }

}