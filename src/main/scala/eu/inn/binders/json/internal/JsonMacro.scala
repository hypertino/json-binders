package eu.inn.binders.json.internal

import java.io.ByteArrayOutputStream

import com.fasterxml.jackson.core.{JsonParser, JsonEncoding, JsonFactory, JsonGenerator}

import scala.language.experimental.macros
import scala.language.reflectiveCalls
import scala.reflect.macros.Context

object JsonMacro {

  def parseJson[C : c.WeakTypeTag, O: c.WeakTypeTag]
    (c: Context): c.Expr[O] = {
    val c0: c.type = c
    val bundle = new {
      val c: c0.type = c0
    } with JsonMacroImpl
    c.Expr[O](bundle.parseJson[C, O])
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

  def addProduct[O: c.WeakTypeTag]
  (c: Context)
    (value: c.Expr[O]): c.Expr[Any] = {
    val c0: c.type = c
    val bundle = new {
      val c: c0.type = c0
    } with JsonMacroImpl
    c.Expr[Any](bundle.addProduct[O](value.tree))
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

  def wrapParser[T](jsonString: String, codeBlock: JsonParser ⇒ T): T = {
    val jf = new JsonFactory()
    val jp = jf.createParser(jsonString)
    try {
      codeBlock(jp)
    } finally {
      jp.close()
    }
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
