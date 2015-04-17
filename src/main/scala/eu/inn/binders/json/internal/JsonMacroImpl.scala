package eu.inn.binders.json.internal

import java.io.OutputStream

import scala.language.experimental.macros
import scala.language.reflectiveCalls
import scala.reflect.macros.Context

trait JsonMacroImpl {
  val c: Context
  import c.universe._

  def parseJson[O: c.WeakTypeTag]: c.Tree = {
    val block = q"""{
      val t = ${c.prefix.tree}
      SerializerFactory.findFactory().withStringParser[${weakTypeOf[O]}](t.jsonString, deserializer=> {
        deserializer.unbind[${weakTypeOf[O]}]
      })
    }"""
    //println(block)
    block
  }

  def toJson[O: c.WeakTypeTag]: c.Tree = {
    val block = q"""{
      val t = ${c.prefix.tree}
      SerializerFactory.findFactory().withStringGenerator(serializer=> {
        serializer.bind[${weakTypeOf[O]}](t.obj)
      })
    }"""
    //println(block)
    block
  }

  def readJson[O: c.WeakTypeTag]: c.Tree = {
    val block = q"""{
      val t = ${c.prefix.tree}
      SerializerFactory.findFactory().withStreamParser[${weakTypeOf[O]}](t.inputStream, deserializer=> {
        deserializer.unbind[${weakTypeOf[O]}]
      })
    }"""
    //println(block)
    block
  }

  def writeJson[O: c.WeakTypeTag](outputStream: c.Expr[OutputStream]): c.Tree = {
    val block = q"""{
      val t = ${c.prefix.tree}
      SerializerFactory.findFactory().withStreamGenerator($outputStream, serializer=> {
        serializer.bind[${weakTypeOf[O]}](t.obj)
      })
    }"""
    //println(block)
    block
  }
}
