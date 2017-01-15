package com.hypertino.binders.json.internal

import java.io.Writer

import com.hypertino.binders.util.MacroAdapter
import MacroAdapter.Context

import scala.language.experimental.macros

private [json] object JsonMacro {
  def parseJson[O: c.WeakTypeTag]
    (c: Context): c.Expr[O] = {
    val c0: c.type = c
    val bundle = new {
      val ctx: c0.type = c0
    } with JsonMacroImpl
    c.Expr[O](bundle.parseJson[O])
  }

  def readJson[O: c.WeakTypeTag]
  (c: Context): c.Expr[O] = {
    val c0: c.type = c
    val bundle = new {
      val ctx: c0.type = c0
    } with JsonMacroImpl
    c.Expr[O](bundle.readJson[O])
  }

  def toJson[O: c.WeakTypeTag]
    (c: Context): c.Expr[String] = {
    val c0: c.type = c
    val bundle = new {
      val ctx: c0.type = c0
    } with JsonMacroImpl
    c.Expr[String](bundle.toJson[O])
  }

  def writeJson[O: c.WeakTypeTag]
  (c: Context)(writer: c.Expr[Writer]): c.Expr[Unit] = {
    val c0: c.type = c
    val bundle = new {
      val ctx: c0.type = c0
    } with JsonMacroImpl
    c.Expr[Unit](bundle.writeJson[O](writer))
  }
}
