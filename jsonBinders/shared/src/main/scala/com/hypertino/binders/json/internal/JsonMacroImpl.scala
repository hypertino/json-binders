package com.hypertino.binders.json.internal

import java.io.Writer

import com.hypertino.binders.util.MacroAdapter
import MacroAdapter.Context

import scala.language.experimental.macros

private [json] trait JsonMacroImpl extends MacroAdapter[Context]{
  import ctx.universe._

  def parseJson[O: WeakTypeTag]: Tree = {
    val t = freshTerm("t")
    val d = freshTerm("d")
    val block = q"""{
      val $t = ${ctx.prefix.tree}
      com.hypertino.binders.json.JsonBindersFactory.findFactory().withStringParser[${weakTypeOf[O]}]($t.jsonString) { case ($d) =>
        $d.unbind[${weakTypeOf[O]}]
      }
    }"""
    //println(block)
    block
  }

  def readJson[O: WeakTypeTag]: Tree = {
    val t = freshTerm("t")
    val d = freshTerm("d")
    val block = q"""{
      val $t = ${ctx.prefix.tree}
      com.hypertino.binders.json.JsonBindersFactory.findFactory().withReader[${weakTypeOf[O]}]($t.reader) { case ($d) =>
        $d.unbind[${weakTypeOf[O]}]
      }
    }"""
    //println(block)
    block
  }

  def toJson[O: WeakTypeTag]: Tree = {
    val t = freshTerm("t")
    val s = freshTerm("s")
    val block = q"""{
      val $t = ${ctx.prefix.tree}
      com.hypertino.binders.json.JsonBindersFactory.findFactory().withStringGenerator { case ($s) =>
        $s.bind[${weakTypeOf[O]}]($t.obj)
      }
    }"""
    //println(block)
    block
  }

  def writeJson[O: WeakTypeTag](writer: Expr[Writer]): Tree = {
    val t = freshTerm("t")
    val s = freshTerm("s")
    val block = q"""{
      val $t = ${ctx.prefix.tree}
      com.hypertino.binders.json.JsonBindersFactory.findFactory().withWriter($writer) { case ($s) =>
        $s.bind[${weakTypeOf[O]}]($t.obj)
      }
    }"""
    //println(block)
    block
  }
}
