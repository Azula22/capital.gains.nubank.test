package services

import models.Stocks
import parser.Codecs.*
import parser.Operation.{Buy, Sell}
import parser.{Command, Tax}
import zio.json.*
import zio.json.ast.Json.Null

import scala.collection.mutable.ArrayBuffer

object CommandService {

  def processCommands(commands: String): String = {
    val parsed = commands.fromJson[List[Command]].getOrElse(List())
    val taxList = ArrayBuffer[zio.json.ast.Json]()
    parsed.map {
      case Command(Buy, cost, quantity)  => (stocks: Stocks) => stocks.buy(quantity, cost)
      case Command(Sell, cost, quantity) => (stocks: Stocks) => stocks.sell(quantity, cost)
    }.foldLeft(Stocks.empty) { case (stocks, operation) =>
      val result = operation.apply(stocks)
      taxList.addOne(
        (result.tax match {
          case Right(v) => Tax(v).toJsonAST
          case Left(error) => parser.Error(error).toJsonAST
        }).getOrElse(Null)
      )
      result.stocks
    }
    taxList.toJson
  }
}
