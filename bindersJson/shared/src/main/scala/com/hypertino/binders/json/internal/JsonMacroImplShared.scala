package com.hypertino.binders.json.internal

import com.hypertino.binders.util.MacroAdapter
import MacroAdapter.Context
import scala.language.experimental.macros

private [json] trait JsonMacroImplShared extends MacroAdapter[Context]{
  import ctx.universe._

  def parseJson[O: WeakTypeTag]: Tree = {
    val t = freshTerm("t")
    val d = freshTerm("d")
    val block = q"""{
      val $t = ${ctx.prefix.tree}
      SerializerFactory.findFactory().withStringParser[${weakTypeOf[O]}]($t.jsonString) { case ($d) =>
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
      SerializerFactory.findFactory().withStringGenerator { case ($s) =>
        $s.bind[${weakTypeOf[O]}]($t.obj)
      }
    }"""
    //println(block)
    block
  }
}
