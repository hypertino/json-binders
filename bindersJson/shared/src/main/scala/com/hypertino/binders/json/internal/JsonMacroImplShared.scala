package com.hypertino.binders.json.internal

import scala.language.experimental.macros
import scala.language.reflectiveCalls
import scala.reflect.macros.Context

private [json] trait JsonMacroImplShared {
  val c: Context
  import c.universe._

  def parseJson[O: c.WeakTypeTag]: c.Tree = {
    val t = fresh("t")
    val d = fresh("d")
    val block = q"""{
      val $t = ${c.prefix.tree}
      SerializerFactory.findFactory().withStringParser[${weakTypeOf[O]}]($t.jsonString) { case ($d) =>
        $d.unbind[${weakTypeOf[O]}]
      }
    }"""
    //println(block)
    block
  }

  def toJson[O: c.WeakTypeTag]: c.Tree = {
    val t = fresh("t")
    val s = fresh("s")
    val block = q"""{
      val $t = ${c.prefix.tree}
      SerializerFactory.findFactory().withStringGenerator { case ($s) =>
        $s.bind[${weakTypeOf[O]}]($t.obj)
      }
    }"""
    //println(block)
    block
  }

  def fresh(prefix: String): TermName = newTermName(c.fresh(prefix))
}
