import org.scalatest.funsuite.AnyFunSuite

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, PrintStream}

class MainTest extends AnyFunSuite {

  test("simulate console input and check console output") {
    val input =
      """[{"operation":"buy", "unit-cost":10.00, "quantity": 10000}, {"operation":"sell", "unit-cost":20.00, "quantity": 5000}]
        |[{"operation":"buy", "unit-cost":20.00, "quantity": 10000}, {"operation":"sell", "unit-cost":10.00, "quantity": 5000}]
        |
        |""".stripMargin

    val inputStream = new ByteArrayInputStream(input.getBytes())
    val outputStream = new ByteArrayOutputStream()

    Console.withIn(inputStream) {
      Console.withOut(new PrintStream(outputStream)) {
        Main.main(Array.empty)
      }
    }

    val output = outputStream.toString.trim

    assert(
      output == """[{"tax":0.00},{"tax":10000.00}]
                  |[{"tax":0.00},{"tax":0.00}]""".stripMargin
    ) // or match exact expected output
  }
}
