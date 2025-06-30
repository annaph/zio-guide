package org.zio.guide.testing.property

import org.zio.guide.testing.Implicits.StringOps
import zio.test.Gen
import zio.{Console, Scope, ZIO, ZIOAppArgs, ZIOAppDefault}

import scala.math.BigDecimal.RoundingMode

object StockGeneratorApp extends ZIOAppDefault {

  private lazy val genTicker: Gen[Any, String] = Gen.asciiString

  private lazy val genPrice: Gen[Any, Double] =
    Gen
      .double(0.01, 100000)
      .map { number =>
        BigDecimal(number)
          .setScale(2, RoundingMode.HALF_UP)
          .toDouble
      }

  private lazy val genCurrency: Gen[Any, Currency] =
    Gen
      .oneOf(
        Gen.const(Currency.USD),
        Gen.const(Currency.EUR),
        Gen.const(Currency.JPY)
      )

  private lazy val genStock: Gen[Any, Stock] =
    for {
      ticker <- genTicker
      price <- genPrice
      currency <- genCurrency
    } yield Stock(ticker, price, currency)

  override def run: ZIO[ZIOAppArgs with Scope, Nothing, Unit] =
    for {
      sample <- genStock.runCollectN(n = 7)
      _ <- Console.printLine(line = s"Generated sample\n${sample mkString "\n"}".withGreenBackground).orDie
    } yield ()

  sealed trait Currency

  private case class Stock(ticker: String, price: Double, currency: Currency) {
    override def toString: String = s"Stock ~~> | $currency <|> $price <|>  $ticker |"
  }

  private object Currency {
    case object USD extends Currency

    case object EUR extends Currency

    case object JPY extends Currency
  }

}
