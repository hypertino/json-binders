package com.hypertino.binders.json

import com.hypertino.binders.json.internal.JsonMacroShared

import scala.language.experimental.macros

object JsonBinders {
  implicit class JsonStringParser(val jsonString: String) extends AnyVal {
    def parseJson[O]: O = macro JsonMacroShared.parseJson[O]
  }

  implicit class JsonStringGenerator[O](val obj: O) extends AnyVal {
    def toJson: String = macro JsonMacroShared.toJson[O]
  }
}
