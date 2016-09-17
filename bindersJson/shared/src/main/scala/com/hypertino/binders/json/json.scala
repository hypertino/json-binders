package com.hypertino.binders

import java.io.{InputStream, OutputStream}

import com.hypertino.binders.json.internal.JsonMacro

import scala.language.experimental.macros

package object json {
  implicit class JsonStringParser(val jsonString: String) extends AnyVal{
    def parseJson[O]: O = macro JsonMacro.parseJson[O]
  }

  implicit class JsonStringGenerator[O](val obj: O) extends AnyVal {
    def toJson: String = macro JsonMacro.toJson[O]
  }

  implicit class JsonStringReader(val inputStream: InputStream) extends AnyVal {
    def readJson[O]: O = macro JsonMacro.readJson[O]
  }

  implicit class JsonStringWriter[O](val obj: O) extends AnyVal {
    def writeJson(outputStream: OutputStream): Unit = macro JsonMacro.writeJson[O]
  }
}
