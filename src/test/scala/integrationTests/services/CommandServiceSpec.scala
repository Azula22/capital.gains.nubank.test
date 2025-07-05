package integrationTests.services

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import zio.json.*
import parser.Codecs.*
import services.CommandService

class CommandServiceSpec extends AnyFlatSpec with Matchers {

  "CommandService.processCommands" should "parse provided valid instructions" in {
    val input =
      """[{"operation":"buy", "unit-cost":10.00, "quantity": 10000},{"operation":"sell", "unit-cost":20.00, "quantity": 5000}]""".stripMargin

    val expected = """[{"tax":0.00},{"tax":10000.00}]"""

    val actual = CommandService.processCommands(input)
    actual.toJsonPretty shouldBe expected.toJsonPretty
  }

  it should "give error message when you sell more then you have" in {
    val input = """[{"operation":"buy", "unit-cost":10, "quantity": 10000}, {"operation":"sell", "unit-cost":20, "quantity": 11000}]""".stripMargin
    val expected = """[{"tax":0.00},{"error":"Can't sell more stocks than you have"}]""".stripMargin

    val actual = CommandService.processCommands(input)
    actual.toJsonAST shouldBe expected.toJsonAST
  }

  it should "give error message when you sell more then you have in 2 operations" in {
    val input = """[{"operation":"buy", "unit-cost": 10, "quantity": 10000}, {"operation":"sell", "unit-cost":20, "quantity": 11000}, {"operation":"sell", "unit-cost": 20, "quantity": 5000}]""".stripMargin
    val expected = """[{"tax":0.00},{"error":"Can't sell more stocks than you have"},{"tax":10000.00}]""".stripMargin

    val actual = CommandService.processCommands(input)
    actual.toJsonAST shouldBe expected.toJsonAST
  }

  it should "handle empty input safely" in {
    val input = ""
    val expected = "[]"
    val actual = CommandService.processCommands(input)
    actual shouldBe expected
  }

  it should "handle invalid input safely" in {
    val input = "1"
    val expected = "[]"
    val actual = CommandService.processCommands(input)
    actual shouldBe expected
  }

}
