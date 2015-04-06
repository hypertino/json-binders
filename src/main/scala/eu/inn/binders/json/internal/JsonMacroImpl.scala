package eu.inn.binders.json.internal

import scala.language.experimental.macros
import scala.language.reflectiveCalls
import scala.reflect.macros.Context

trait JsonMacroImpl {
  val c: Context
  import c.universe._

  def parseJson[O: c.WeakTypeTag]: c.Tree = {
    val block = q"""{
      val t = ${c.prefix.tree}
      SerializerFactory.findFactory().withParser[${weakTypeOf[O]}](t.jsonString, deserializer=> {
        deserializer.unbind[${weakTypeOf[O]}]
      })
    }"""
    //println(block)
    block
  }

  def toJson[O: c.WeakTypeTag]: c.Tree = {
    val block = q"""{
      val t = ${c.prefix.tree}
      SerializerFactory.findFactory().withGenerator(serializer=> {
        serializer.bind[${weakTypeOf[O]}](t.obj)
      })
    }"""
    //println(block)
    block
  }
}
