import services.CommandService

import scala.annotation.tailrec
import scala.collection.mutable.ArrayBuffer
import scala.io.StdIn.readLine

object Main {
  def main(args: Array[String]): Unit = {

    val toPrint = ArrayBuffer[String]()

    @tailrec
    def processInput(input: String): Unit = {
      if input.trim.nonEmpty then {
        toPrint.addOne(CommandService.processCommands(input))
        processInput(readLine())
      }
    }

    processInput(readLine())
    
    println(toPrint.mkString("\n"))
  }
}
