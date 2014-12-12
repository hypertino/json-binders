package eu.inn.binders.json.internal

import eu.inn.binders.json.JsonSerializer

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

    val thisTerm = newTermName(c.fresh("$this"))
    val objTerm = newTermName("$obj")
    val jsonGeneratorTerm = newTermName(c.fresh("$jg"))
    val jsonSerializerTerm = newTermName(c.fresh("$js"))

    val vals = List(
      ValDef(Modifiers(), thisTerm, TypeTree(), c.prefix.tree),
      ValDef(Modifiers(), objTerm, TypeTree(), Select(Ident(thisTerm), newTermName("obj")))
    )

    val serializeBlock =
      Block(
        // val js = JsonSerializer.apply(jg)
        ValDef(Modifiers(), jsonSerializerTerm, TypeTree(),
          Apply(
            Select(Ident(typeOf[JsonSerializer.type].termSymbol), newTermName("apply")),
            List(Ident(jsonGeneratorTerm))
          )
        ),
        // js.bind[O](this.obj)
        Apply(
          TypeApply(
            Select(Ident(jsonSerializerTerm), newTermName("bind")),
            List(Ident(weakTypeOf[O].typeSymbol))
          ),
          List(Ident(objTerm))
        )
      )

    val genCodeFunc = Function(
      List(ValDef(Modifiers(Flag.PARAM), jsonGeneratorTerm, TypeTree(), EmptyTree)),
      serializeBlock
    )

    val block = Block(vals,
      Apply(Select(Ident(typeOf[JsonMacro.type].termSymbol), newTermName("wrapObjectGen")), List(genCodeFunc))
    )
    println(block)
    block
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
