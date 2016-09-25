package com.hypertino.binders.json.internal

import com.hypertino.binders.util.MacroAdapter
import MacroAdapter.Context
import scala.language.experimental.macros

private [json] object JsonMacroShared {
  def parseJson[O: c.WeakTypeTag]
    (c: Context): c.Expr[O] = {
    val c0: c.type = c
    val bundle = new {
      val ctx: c0.type = c0
    } with JsonMacroImplShared
    c.Expr[O](bundle.parseJson[O])
  }

  def toJson[O: c.WeakTypeTag]
    (c: Context): c.Expr[String] = {
    val c0: c.type = c
    val bundle = new {
      val ctx: c0.type = c0
    } with JsonMacroImplShared
    c.Expr[String](bundle.toJson[O])
  }
}
