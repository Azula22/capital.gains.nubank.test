package models

case class OperationResult(stocks: Stocks, tax: Either[String, BigDecimal])
