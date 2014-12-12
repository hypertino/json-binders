package eu.inn.binders.json.internal

import java.io.ByteArrayOutputStream

import com.fasterxml.jackson.core.{JsonEncoding, JsonFactory, JsonGenerator}
import eu.inn.binders.naming.Converter

import scala.language.experimental.macros
import scala.language.reflectiveCalls
import scala.reflect.macros.Context

object JsonMacro {

  def parseJson[O: c.WeakTypeTag]
    (c: Context): c.Expr[O] = {
    val c0: c.type = c
    val bundle = new {
      val c: c0.type = c0
    } with JsonMacroImpl
    c.Expr[O](bundle.parseJson[O])
  }

  def toJson[C : c.WeakTypeTag, O: c.WeakTypeTag]
    (c: Context): c.Expr[String] = {
    val c0: c.type = c
    val bundle = new {
      val c: c0.type = c0
    } with JsonMacroImpl
    c.Expr[String](bundle.toJson[C, O])
  }

  def setProduct[O: c.WeakTypeTag]
    (c: Context)
    (name: c.Expr[String], value: c.Expr[O]): c.Expr[Any] = {
    val c0: c.type = c
    val bundle = new {
      val c: c0.type = c0
    } with JsonMacroImpl
    c.Expr[Any](bundle.setProduct[O](name.tree, value.tree))
  }

  def setSequence[O: c.WeakTypeTag]
    (c: Context)
    (name: c.Expr[String], value: c.Expr[O]): c.Expr[Any] = {
    val c0: c.type = c
    val bundle = new {
      val c: c0.type = c0
    } with JsonMacroImpl
    c.Expr[Any](bundle.setSequence[O](name.tree, value.tree))
  }

  def wrapGenerator(codeBlock: JsonGenerator ⇒ Unit): String = {
    val jf = new JsonFactory()
    val ba = new ByteArrayOutputStream()
    try {
      val jg = jf.createGenerator(ba, JsonEncoding.UTF8)
      try {
        codeBlock(jg)
      }
      finally {
        jg.close()
      }
    }
    finally {
      ba.close()
    }
    ba.toString("UTF-8")
  }

  def wrapObjectGen(codeBlock: JsonGenerator ⇒ Unit): String = {
    wrapGenerator(jsonGenerator ⇒ {
      jsonGenerator.writeStartObject()
      codeBlock(jsonGenerator)
      jsonGenerator.writeEndObject()
    })
  }

  def wrapArrayGen(codeBlock: JsonGenerator ⇒ Unit): String = {
    wrapGenerator(jsonGenerator ⇒ {
      jsonGenerator.writeStartArray()
      codeBlock(jsonGenerator)
      jsonGenerator.writeEndArray()
    })
  }
}
