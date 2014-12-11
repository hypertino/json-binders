package eu.inn.binders

import eu.inn.binders.json.internal.JsonMacro
import scala.language.experimental.macros

package object json {
  implicit class JsonStringParser(srt: String) {
    def parseJson[O]: O = macro JsonMacro.parseJson[O]
  }

  implicit class JsonGeneratorProduct[C, O <: Product](obj: O) {
    def toJson: String = macro JsonMacro.toJson[O]
  }
}
