package com.hypertino.binders.json.internal

import scala.language.experimental.macros
import scala.language.reflectiveCalls
import scala.reflect.macros.Context

private [json] object JsonMacroShared {
  def parseJson[O: c.WeakTypeTag]
    (c: Context): c.Expr[O] = {
    val c0: c.type = c
    val bundle = new {
      val c: c0.type = c0
    } with JsonMacroImplShared
    c.Expr[O](bundle.parseJson[O])
  }

  def toJson[O: c.WeakTypeTag]
    (c: Context): c.Expr[String] = {
    val c0: c.type = c
    val bundle = new {
      val c: c0.type = c0
    } with JsonMacroImplShared
    c.Expr[String](bundle.toJson[O])
  }
}
