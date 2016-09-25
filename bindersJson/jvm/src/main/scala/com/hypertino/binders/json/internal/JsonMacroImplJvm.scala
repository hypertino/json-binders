package com.hypertino.binders.json.internal

import java.io.OutputStream

import com.hypertino.binders.util.MacroAdapter
import MacroAdapter.Context
import scala.language.experimental.macros

private [json] trait JsonMacroImplJvm extends MacroAdapter[Context] {
  import ctx.universe._

  def readJson[O: WeakTypeTag]: Tree = {
    val t = freshTerm("t")
    val d = freshTerm("d")
    val block = q"""{
      val $t = ${ctx.prefix.tree}
      SerializerFactory.findFactory().withStreamParser[${weakTypeOf[O]}]($t.inputStream) { case ($d) =>
        $d.unbind[${weakTypeOf[O]}]
      }
    }"""
    //println(block)
    block
  }

  def writeJson[O: WeakTypeTag](outputStream: Expr[OutputStream]): Tree = {
    val t = freshTerm("t")
    val s = freshTerm("s")
    val block = q"""{
      val $t = ${ctx.prefix.tree}
      SerializerFactory.findFactory().withStreamGenerator($outputStream) { case ($s) =>
        $s.bind[${weakTypeOf[O]}]($t.obj)
      }
    }"""
    //println(block)
    block
  }
}
