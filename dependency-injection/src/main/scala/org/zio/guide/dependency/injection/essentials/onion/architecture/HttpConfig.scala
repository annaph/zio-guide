package org.zio.guide.dependency.injection.essentials.onion.architecture

import org.zio.guide.dependency.injection.essentials.onion.architecture.Implicits.threeSeconds
import zio.{Duration, ZIO, ZLayer}

trait HttpConfig {
  def responseTime: Duration
}

object HttpConfig {
  final case class HttpConfigLive(responseTime: Duration) extends HttpConfig

  object HttpConfigLive {
    val layer: ZLayer[Any, Nothing, HttpConfig] =
      ZLayer.apply {
        ZIO.succeed(HttpConfigLive(threeSeconds))
      }
  }

}
