package eu.inn.binders.json.internal

import scala.language.experimental.macros
import scala.language.reflectiveCalls
import scala.reflect.macros.Context

trait JsonMacroImpl {
  val c: Context

  import c.universe._

  def setProduct[O: c.WeakTypeTag](name: c.Tree, value: c.Tree): c.Tree = {
    val block = Block(
      List(
        Apply(Select(c.prefix.tree, newTermName("beginObject")),List(name)),
        Apply(Select(c.prefix.tree, newTermName("bind")), List(value))
      ),
      Apply(Select(c.prefix.tree, newTermName("endObject")),List())
    )
    // println(block)
    block
  }

  def setSequence[O: c.WeakTypeTag](name: c.Tree, value: c.Tree): c.Tree = {
    val elemTerm = newTermName(c.fresh("$elem"))
    val block = Block(
      List(
        Apply(Select(c.prefix.tree, newTermName("beginArray")),List(name)),
        Apply(Select(value, newTermName("map")), List(
          Function( // element ⇒
            List(ValDef(Modifiers(Flag.PARAM), elemTerm, TypeTree(), EmptyTree)),
            Apply(Select(c.prefix.tree, newTermName("bindArgs")), List(Ident(elemTerm)))
          )
        ))
        //
      ),
      Apply(Select(c.prefix.tree, newTermName("endArray")),List())
    )
    // println(block)
    block
  }
}
