package models

import models.Stocks.{nontaxableLimit, operationFailedTax, tax, zeroTax}

import scala.math.BigDecimal.RoundingMode

case class Stocks(private val quantity: Int, private val weightedPrice: BigDecimal, private val losses: BigDecimal) {

  def buy(newStockQuantity: Int, newStockPrice: BigDecimal): OperationResult = {
    if (newStockPrice == 0) OperationResult(this, zeroTax) // getting stocks for free is not supported
    OperationResult(
      stocks = copy(
        quantity = quantity + newStockQuantity,
        weightedPrice = newWeightedPrice(newStockQuantity, newStockPrice)
      ),
      tax = zeroTax
    )
  }

  def sell(sellQuantity: Int, sellPrice: BigDecimal): OperationResult = {
    val profit = (sellPrice - weightedPrice) * sellQuantity
    val updated = copy(quantity = quantity - sellQuantity)
    profit match
      case _ if sellQuantity > quantity => OperationResult(this, operationFailedTax)
      case _ if profit < 0                                  => OperationResult(
        stocks = updated.copy(losses = losses + sellQuantity * (weightedPrice - sellPrice)),
        tax = zeroTax
      )
      case _ if sellPrice * sellQuantity <= nontaxableLimit => OperationResult(
        stocks = updated,
        tax = zeroTax
      )
      case _ if profit <= losses                            => OperationResult(
        stocks = updated.copy(losses = losses - profit),
        tax = zeroTax
      )
      case _                                                => OperationResult(
        stocks = updated.copy(losses = 0),
        tax = Right(((profit - losses) * tax).setScale(2, RoundingMode.HALF_UP))
      )
  }

  private def newWeightedPrice(newStocksCount: Int, newStockPrice: BigDecimal): BigDecimal =
      ((quantity * weightedPrice + newStocksCount * newStockPrice) / (quantity + newStocksCount))
        .setScale(2, RoundingMode.HALF_UP)
}

object Stocks {

  val zeroTax: Either[String, BigDecimal] = Right(BigDecimal(0).setScale(2))
  val operationFailedTax: Either[String, BigDecimal] = Left("Can't sell more stocks than you have")
  val tax = 0.2
  val nontaxableLimit = 20_000

  val empty: Stocks = Stocks(0, 0, 0)

}

