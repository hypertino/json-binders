package eu.inn.binders.json.internal

import scala.language.experimental.macros
import scala.language.reflectiveCalls
import scala.reflect.macros.Context

trait JsonMacroImpl {
  val c: Context

  import c.universe._

  def setProduct[O: c.WeakTypeTag](name: c.Tree, value: c.Tree): c.Tree = {

    val thisTerm = newTermName(c.fresh("$this"))
    val jsonGeneratorTerm = newTermName(c.fresh("$jg"))

    val vals = List(
      ValDef(Modifiers(), thisTerm, TypeTree(), c.prefix.tree),
      ValDef(Modifiers(), jsonGeneratorTerm, TypeTree(), Select(Ident(thisTerm), newTermName("jsonGenerator")))
    )

    //val tpe = weakTypeTag[O].tpe
    //println("setters: " + tpe)
    val block = Block(
      vals ++
      List(
        Apply(Select(Ident(jsonGeneratorTerm), newTermName("writeObjectFieldStart")),List(name)),
        Apply(Select(Ident(thisTerm), newTermName("bind")), List(value)),
        Apply(Select(Ident(jsonGeneratorTerm), newTermName("writeEndObject")),List())
      ),
      Literal(Constant())
    )

    println(block)
    block
  }
}
