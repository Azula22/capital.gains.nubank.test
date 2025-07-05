package parser

import zio.json.*

object Codecs {
  implicit val operationDecoder: JsonDecoder[Operation] = JsonDecoder[String].map(v => Operation.valueOf(v.capitalize))
  implicit val commandDecoder: JsonDecoder[Command] = DeriveJsonDecoder.gen[Command]
  implicit val taxEncoder: JsonEncoder[Tax] = DeriveJsonEncoder.gen[Tax]
  implicit val errorEncoder: JsonEncoder[parser.Error] = DeriveJsonEncoder.gen[parser.Error]
}