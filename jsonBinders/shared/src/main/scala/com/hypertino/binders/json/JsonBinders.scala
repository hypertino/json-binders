package com.hypertino.binders.json

import java.io.{InputStream, Reader, Writer}

import com.hypertino.binders.json.internal.JsonMacro

import scala.language.experimental.macros

object JsonBinders {
  implicit class JsonStringReader(val reader: Reader) extends AnyVal {
    def readJson[O]: O = macro JsonMacro.readJson[O]
  }

  implicit class JsonStringWriter[O](val obj: O) extends AnyVal {
    def writeJson(writer: Writer): Unit = macro JsonMacro.writeJson[O]
  }

  implicit class JsonStringParser(val jsonString: String) extends AnyVal {
    def parseJson[O]: O = macro JsonMacro.parseJson[O]
  }

  implicit class JsonStringGenerator[O](val obj: O) extends AnyVal {
    def toJson: String = macro JsonMacro.toJson[O]
  }
}
