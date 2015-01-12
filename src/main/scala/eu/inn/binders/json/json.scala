package eu.inn.binders

import eu.inn.binders.json.internal.JsonMacro
import eu.inn.binders.naming.{PlainConverter, Converter}
import scala.language.experimental.macros
import scala.reflect.runtime.universe._

package object json {
  implicit class JsonStringParser(val jsonString: String) {
    def parseJson[O]: O = macro JsonMacro.parseJson[PlainConverter, O]
//    def parseJsonWith[C <: Converter,O]: O = macro JsonMacro.parseJson[C,O]
  }

  implicit class JsonStringGenerator[O](val obj: O) {
    def toJson: String = macro JsonMacro.toJson[PlainConverter,O]
//    def toJsonWith[C <: Converter] = macro JsonMacro.toJson[C,O]
  }
}
