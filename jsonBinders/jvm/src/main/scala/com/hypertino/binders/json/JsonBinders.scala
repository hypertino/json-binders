package com.hypertino.binders.json

import java.io.{InputStream, OutputStream}

import com.hypertino.binders.json.internal.{JsonMacroJvm, JsonMacroShared}
import scala.language.experimental.macros

object JsonBinders {
  implicit class JsonStringReader(val inputStream: InputStream) extends AnyVal {
    def readJson[O]: O = macro JsonMacroJvm.readJson[O]
  }

  implicit class JsonStringWriter[O](val obj: O) extends AnyVal {
    def writeJson(outputStream: OutputStream): Unit = macro JsonMacroJvm.writeJson[O]
  }

  implicit class JsonStringParser(val jsonString: String) extends AnyVal {
    def parseJson[O]: O = macro JsonMacroShared.parseJson[O]
  }

  implicit class JsonStringGenerator[O](val obj: O) extends AnyVal {
    def toJson: String = macro JsonMacroShared.toJson[O]
  }
}
