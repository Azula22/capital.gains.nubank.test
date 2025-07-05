package models

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class StocksTest extends AnyFunSuite with Matchers {

  test("Case #1: Should not apply tax when the total sale amount is under $20,000") {
    val result = runOperations {
      List(_.buy(100, 10), _.sell(50, 15), _.sell(50, 15))
    }
    result shouldBe List(0, 0, 0)
  }

  test("Case #2: Should apply tax on profit when there are no prior losses, and skip tax on later loss") {
    val result = runOperations {
      List(_.buy(10000, 10), _.sell(5000, 20), _.sell(5000, 5))
    }
    result shouldBe List(0, 10000, 0)
  }

  test("Case #3: Should carry over a loss and deduct it from a later profit before applying tax") {
    val result = runOperations {
      List(_.buy(10000, 10), _.sell(5000, 5), _.sell(3000, 20))
    }
    result shouldBe List(0, 0, 1000)
  }

  test("Case #4: Should not apply tax when the average cost equals the selling price") {
    val result = runOperations {
      List(_.buy(10000, 10), _.buy(5000, 25), _.sell(10000, 15))
    }
    result shouldBe List(0, 0, 0)
  }

  test("Case #5: Should apply tax only on the second profitable sale after a break-even sale") {
    val result = runOperations {
      List(_.buy(10000, 10), _.buy(5000, 25), _.sell(10000, 15), _.sell(5000, 25))
    }
    result shouldBe List(0, 0, 0, 10000)
  }

  test("Case #6: Should deduct accumulated losses before taxing subsequent profits") {
    val result = runOperations {
      List(_.buy(10000, 10), _.sell(5000, 2), _.sell(2000, 20), _.sell(2000, 20), _.sell(1000, 25))
    }
    result shouldBe List(0, 0, 0, 0, 3000)
  }

  test("Case #7: Should handle losses, update average cost with new purchases, and apply tax accordingly") {
    val result = runOperations {
      List(
        _.buy(10000, 10),
        _.sell(5000, 2),
        _.sell(2000, 20),
        _.sell(2000, 20),
        _.sell(1000, 25),
        _.buy(10000, 20),
        _.sell(5000, 15),
        _.sell(4350, 30),
        _.sell(650, 30)
      )
    }
    result shouldBe List(0, 0, 0, 0, 3000, 0, 0, 3700, 0)
  }

  test("Case #8: Should apply correct tax for large volume profits across multiple buy-sell cycles") {
    val result = runOperations {
      List(_.buy(10000, 10), _.sell(10000, 50), _.buy(10000, 20), _.sell(10000, 50))
    }
    result shouldBe List(0, 80000, 0, 60000)
  }

  test("Case #9: Should handle mixed operations, deduct losses, and apply tax only when applicable") {
    val result = runOperations {
      List(
        _.buy(10, 5000),
        _.sell(5, 4000),
        _.buy(5, 15000),
        _.buy(2, 4000),
        _.buy(2, 23000),
        _.sell(1, 20000),
        _.sell(10, 12000),
        _.sell(3, 15000)
      )

    }
    result shouldBe List(0, 0, 0, 0, 0, 0, 1000, 2400)
  }

  test("Multiple simulations don't impact each other (Case #1 + #2)") {
    val r1 = runOperations {
      List(_.buy(100, 10), _.sell(50, 15), _.sell(50, 15))
    }

    val r2 = runOperations {
      List(_.buy(10000, 10), _.sell(5000, 20), _.sell(5000, 5))
    }

    r1 shouldBe List(0, 0, 0)
    r2 shouldBe List(0, 10000, 0)
  }

  test("Weighted average price must be rounded up") {
    val result = runOperations {
      List(_.buy(10000, 20), _.buy(5000, 10), _.sell(15000, 17.03))
    }
    result shouldBe List(0, 0, 1080)
  }

  test("Buying 0 stocks should not break the logic") {
    val result = runOperations {
      List(_.buy(10000, 20), _.buy(5000, 10), _.buy(0, 10), _.sell(15000, 17.03))
    }
    result shouldBe List(0, 0, 0, 1080)
  }

  test("Selling 0 stocks should not break the logic") {
    val result = runOperations {
      List(_.buy(10000, 20), _.buy(5000, 10), _.sell(0, 17.03))
    }
    result shouldBe List(0, 0, 0)
  }

  test("Buying stocks for the price 0 is not supported and ignored") {
    val result = runOperations {
      List(_.buy(10000, 20), _.buy(5000, 10), _.buy(5000, 0), _.sell(0, 17.03))
    }
    result shouldBe List(0, 0, 0, 0)
  }

  test("Selling stocks for the price 0 should not break the logic") {
    val result = runOperations {
      List(_.buy(10000, 20), _.buy(5000, 10), _.sell(0, 0))
    }
    result shouldBe List(0, 0, 0)
  }
  
  private def runOperations(ops: List[Stocks => OperationResult]): List[BigDecimal] = {
    val (_, taxes) = ops.foldLeft(Stocks.empty, List.empty[BigDecimal]) {
      case ((stocks, accTaxes), fun) =>
        val OperationResult(updatedStocks, tax) = fun.apply(stocks)
        (updatedStocks, accTaxes :+ tax.getOrElse(0))
    }
    taxes
  }

}