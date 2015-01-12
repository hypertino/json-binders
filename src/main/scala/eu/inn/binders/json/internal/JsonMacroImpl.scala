package eu.inn.binders.json.internal

import scala.language.experimental.macros
import scala.language.reflectiveCalls
import scala.reflect.macros.Context

trait JsonMacroImpl {
  val c: Context
  import c.universe._

  def parseJson[C : c.WeakTypeTag, O: c.WeakTypeTag]: c.Tree = {
    val block = q"""{
      val t = ${c.prefix.tree}
      var o: Option[${weakTypeOf[O]}] = None
      eu.inn.binders.json.internal.JsonMacro.withFactory(factory => {
        factory.withParser[${weakTypeOf[O]}](t.jsonString, deserializer=> {
          o = Some(deserializer.unbind[${weakTypeOf[O]}])
        })
      })
      o.get
    }"""
    //println(block)
    block
  }

  def toJson[C : c.WeakTypeTag, O: c.WeakTypeTag]: c.Tree = {
    val block = q"""{
      val t = ${c.prefix.tree}
      var s: Option[String] = None
      eu.inn.binders.json.internal.JsonMacro.withFactory(factory => {
        factory.withGenerator(serializer=> {
          s = Some(serializer.bind[${weakTypeOf[O]}](t.obj))
        })
      })
      s.get
    }"""
    println(block)
    block
  }

  def writeMap[S: c.WeakTypeTag, O: c.WeakTypeTag](value: c.Tree): c.Tree = {
    val block = q"""{
      val serializer = ${c.prefix.tree}
      val it = $value
      serializer.beginObject()
      it.foreach(kv => {
        serializer.getFieldSerializer(kv._1).map(_.bind(kv._2))
      })
      serializer.endObject()
    }"""
    //println(block)
    block
  }

  def readMap[S: c.WeakTypeTag, O: c.WeakTypeTag]: c.Tree = {
    val block = q"""{
      val deserializer = ${c.prefix.tree}
      deserializer.iterator().map{ el =>
        (el.fieldName.get, el.unbind[${weakTypeOf[O]}])
      }.toMap
    }"""
    //println(block)
    block
  }
}
