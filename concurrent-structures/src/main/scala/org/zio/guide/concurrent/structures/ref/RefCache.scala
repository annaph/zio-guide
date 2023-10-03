package org.zio.guide.concurrent.structures.ref

import zio.{Ref, UIO, ZIO}

trait RefCache[K, V] {

  def getOrElseCompute(k: K)(f: K => V): UIO[Ref[V]]

}

object RefCache {

  def make[K, V]: UIO[RefCache[K, V]] =
    Ref.Synchronized
      .make(Map.empty[K, Ref[V]])
      .map { mapRef =>
        new RefCache[K, V] {
          override def getOrElseCompute(k: K)(f: K => V): UIO[Ref[V]] = {
            mapRef.modifyZIO { map =>
              map.get(k) match {
                case Some(ref) =>
                  ZIO.succeed(ref, map)
                case None =>
                  Ref.make(f(k)).map(ref => (ref, map + (k -> ref)))
              }
            }
          }
        }
      }

}
