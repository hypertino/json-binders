package eu.inn.binders

import eu.inn.binders.json.internal.JsonMacro
import eu.inn.binders.naming.{PlainConverter, Converter}
import scala.language.experimental.macros
import scala.reflect.runtime.universe._

package object json {
  implicit class JsonStringParser(srt: String) {
    def parseJson[O]: O = macro JsonMacro.parseJson[O]
  }

  implicit class JsonGeneratorProduct[O <: Product](val obj: O) {
    def toJson: String = macro JsonMacro.toJson[PlainConverter,O]
    def toJsonWith[C <: Converter] = macro JsonMacro.toJson[C,O]
  }
}
