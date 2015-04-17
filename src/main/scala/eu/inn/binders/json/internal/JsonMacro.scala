package eu.inn.binders.json.internal

import java.io.OutputStream

import scala.language.experimental.macros
import scala.language.reflectiveCalls
import scala.reflect.macros.Context

object JsonMacro {
  def parseJson[O: c.WeakTypeTag]
    (c: Context): c.Expr[O] = {
    val c0: c.type = c
    val bundle = new {
      val c: c0.type = c0
    } with JsonMacroImpl
    c.Expr[O](bundle.parseJson[O])
  }

  def toJson[O: c.WeakTypeTag]
    (c: Context): c.Expr[String] = {
    val c0: c.type = c
    val bundle = new {
      val c: c0.type = c0
    } with JsonMacroImpl
    c.Expr[String](bundle.toJson[O])
  }

  def readJson[O: c.WeakTypeTag]
  (c: Context): c.Expr[O] = {
    val c0: c.type = c
    val bundle = new {
      val c: c0.type = c0
    } with JsonMacroImpl
    c.Expr[O](bundle.readJson[O])
  }

  def writeJson[O: c.WeakTypeTag]
  (c: Context)(outputStream: c.Expr[OutputStream]): c.Expr[Unit] = {
    val c0: c.type = c
    val bundle = new {
      val c: c0.type = c0
    } with JsonMacroImpl
    c.Expr[Unit](bundle.writeJson[O](outputStream))
  }
}
