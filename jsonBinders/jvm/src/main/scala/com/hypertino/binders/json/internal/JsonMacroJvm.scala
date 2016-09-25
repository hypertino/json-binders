package com.hypertino.binders.json.internal

import java.io.OutputStream

import com.hypertino.binders.util.MacroAdapter
import MacroAdapter.Context
import scala.language.experimental.macros

private [json] object JsonMacroJvm {
  def readJson[O: c.WeakTypeTag]
  (c: Context): c.Expr[O] = {
    val c0: c.type = c
    val bundle = new {
      val ctx: c0.type = c0
    } with JsonMacroImplJvm
    c.Expr[O](bundle.readJson[O])
  }

  def writeJson[O: c.WeakTypeTag]
  (c: Context)(outputStream: c.Expr[OutputStream]): c.Expr[Unit] = {
    val c0: c.type = c
    val bundle = new {
      val ctx: c0.type = c0
    } with JsonMacroImplJvm
    c.Expr[Unit](bundle.writeJson[O](outputStream))
  }
}
