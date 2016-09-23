package com.hypertino.binders.json.internal

import java.io.OutputStream

import scala.language.experimental.macros
import scala.language.reflectiveCalls
import scala.reflect.macros.Context

private [json] trait JsonMacroImplJvm {
  val c: Context
  import c.universe._

  def readJson[O: c.WeakTypeTag]: c.Tree = {
    val t = fresh("t")
    val d = fresh("d")
    val block = q"""{
      val $t = ${c.prefix.tree}
      SerializerFactory.findFactory().withStreamParser[${weakTypeOf[O]}]($t.inputStream) { case ($d) =>
        $d.unbind[${weakTypeOf[O]}]
      }
    }"""
    //println(block)
    block
  }

  def writeJson[O: c.WeakTypeTag](outputStream: c.Expr[OutputStream]): c.Tree = {
    val t = fresh("t")
    val s = fresh("s")
    val block = q"""{
      val $t = ${c.prefix.tree}
      SerializerFactory.findFactory().withStreamGenerator($outputStream) { case ($s) =>
        $s.bind[${weakTypeOf[O]}]($t.obj)
      }
    }"""
    //println(block)
    block
  }

  def fresh(prefix: String): TermName = newTermName(c.fresh(prefix))
}
