package eu.inn.binders.json.internal

import scala.language.experimental.macros
import scala.language.reflectiveCalls
import scala.reflect.macros.Context

trait JsonMacroImpl {
  val c: Context
  import c.universe._

  def parseJson[C : c.WeakTypeTag, O: c.WeakTypeTag]: c.Tree = {
    val block = q"""{
      import eu.inn.binders.json._
      import com.fasterxml.jackson.core._
      val t = ${c.prefix.tree}
      internal.JsonMacro.wrapParser(t.jsonString, (jp: JsonParser)=> {
        val deserializer = JsonDeserializer[${weakTypeOf[C].typeSymbol}](jp)
        deserializer.unbind[${weakTypeOf[O].typeSymbol}]
      })
    }"""
    //println(block)
    block
  }

  def toJson[C : c.WeakTypeTag, O: c.WeakTypeTag]: c.Tree = {
    val block = q"""{
      import eu.inn.binders.json._
      import com.fasterxml.jackson.core._
      val t = ${c.prefix.tree}
      internal.JsonMacro.wrapGenerator((jp: JsonGenerator)=> {
        val serializer = JsonSerializer[${weakTypeOf[C].typeSymbol}](jp)
        serializer.bind[${weakTypeOf[O].typeSymbol}](t.obj)
      })
    }"""
    //println(block)
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
        (el.fieldName.get, el.unbind[${weakTypeOf[O].typeSymbol}])
      }.toMap
    }"""
    //println(block)
    block
  }
}
