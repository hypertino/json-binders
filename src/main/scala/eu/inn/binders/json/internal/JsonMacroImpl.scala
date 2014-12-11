package eu.inn.binders.json.internal

import scala.language.experimental.macros
import scala.language.reflectiveCalls
import scala.reflect.macros.Context

trait JsonMacroImpl {
  val c: Context

  import c.universe._

  def parseJson[O: c.WeakTypeTag]: c.Tree = {
    Block()
  }

  def toJson[O: c.WeakTypeTag]: c.Tree = {

    val jsonMacroTerm = JsonMacro.getClass.

    Apply(Select(Ident(jsonMacroTerm), newTermName("apply")), List())

    /*
    *
    * */

    Literal(Constant("a"))
  }

  protected def writeNull[O: c.WeakTypeTag](name: c.Tree, value: c.Tree, nonNullBlock: c.Tree): c.Tree = {
    if (weakTypeTag[O].tpe <:< typeOf[Option[_]]) {
      If(
        Select(value, newTermName("isDefined")),
        nonNullBlock,
        Apply(Select(c.prefix.tree, newTermName("setNull")),List(name))
      )
    }
    else
      nonNullBlock
  }

  protected def optionGetter[O: c.WeakTypeTag](value: c.Tree): c.Tree = {
    if (weakTypeTag[O].tpe <:< typeOf[Option[_]]) {
      Select(value, newTermName("get"))
    }
    else
      value
  }

  def setProduct[O: c.WeakTypeTag](name: c.Tree, value: c.Tree): c.Tree = {
    val nonNullBlock = Block(
      List(
        Apply(Select(c.prefix.tree, newTermName("beginObject")),List(name)),
        Apply(Select(c.prefix.tree, newTermName("bind")), List(optionGetter[O](value)))
      ),
      Apply(Select(c.prefix.tree, newTermName("endObject")),List())
    )
    val block = writeNull[O](name, value, nonNullBlock)
    // println(block)
    block
  }

  def setSequence[O: c.WeakTypeTag](name: c.Tree, value: c.Tree): c.Tree = {
    val elemTerm = newTermName(c.fresh("$elem"))
    val nonNullBlock = Block(
      List(
        Apply(Select(c.prefix.tree, newTermName("beginArray")),List(name)),
        Apply(Select(optionGetter[O](value), newTermName("map")), List(
          Function( // element ⇒
            List(ValDef(Modifiers(Flag.PARAM), elemTerm, TypeTree(), EmptyTree)),
            Apply(Select(c.prefix.tree, newTermName("bindArgs")), List(Ident(elemTerm)))
          )
        ))
        //
      ),
      Apply(Select(c.prefix.tree, newTermName("endArray")),List())
    )
    val block = writeNull[O](name, value, nonNullBlock)
    // println(block)
    block
  }
}
