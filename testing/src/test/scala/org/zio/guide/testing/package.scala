package org.zio.guide

import zio.Duration

import scala.concurrent.duration.{Duration => ScalaDuration}
import scala.language.implicitConversions

package object testing {

  object Implicits {
    implicit def toZIODuration(scalaDuration: ScalaDuration): Duration =
      Duration.fromScala(scalaDuration)
  }

}
