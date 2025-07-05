package parser

import zio.json.{KebabCase, jsonMemberNames}

@jsonMemberNames(KebabCase)
case class Command(operation: Operation, unitCost: BigDecimal, quantity: Int)

sealed trait TaxResult
case class Tax(tax: BigDecimal) extends TaxResult
case class Error(error: String) extends TaxResult
