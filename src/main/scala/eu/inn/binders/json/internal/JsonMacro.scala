package eu.inn.binders.json.internal

import scala.language.experimental.macros
import scala.language.reflectiveCalls
import scala.reflect.macros.Context

object JsonMacro {

  def setProduct[O: c.WeakTypeTag]
  (c: Context)
  (name: c.Expr[String], value: c.Expr[O]): c.Expr[Any] = {

    val c0: c.type = c
    val bundle = new {
      val c: c0.type = c0
    } with JsonMacroImpl
    c.Expr[Any](bundle.setProduct[O](name.tree, value.tree))
  }

}
