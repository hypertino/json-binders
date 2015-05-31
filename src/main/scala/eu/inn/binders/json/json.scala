package eu.inn.binders

import java.io.{OutputStream, InputStream}

import eu.inn.binders.json.internal.JsonMacro
import eu.inn.binders.naming.{PlainConverter, Converter}
import scala.language.experimental.macros
import scala.reflect.runtime.universe._

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
