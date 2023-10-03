package org.zio.guide.concurrent.structures.ref

import zio.{UIO, ZIO}

import java.util.concurrent.atomic.AtomicReference
import scala.annotation.{tailrec, unused}

trait MyRef[T] {

  def modify[S](f: T => (S, T)): UIO[S]

  def get: UIO[T] =
    modify(t => (t, t))

  @unused
  def set(t: T): UIO[Unit] =
    modify(_ => ((), t))

  def update(f: T => T): UIO[Unit] =
    modify(t => ((), f(t)))

}

object MyRef {

  def make[T](t: T): UIO[MyRef[T]] = ZIO.succeed {
    new MyRef[T] {
      val reference = new AtomicReference[T](t)

      override def modify[S](f: T => (S, T)): UIO[S] = ZIO.succeed {
        updateReference(f)
      }

      @tailrec
      private def updateReference[S](f: T => (S, T)): S = {
        val currT = reference.get
        val (s, newT) = f(currT)

        val isUpdated = reference.compareAndSet(currT, newT)

        if (!isUpdated) updateReference(f)
        else s
      }
    }
  }

}
