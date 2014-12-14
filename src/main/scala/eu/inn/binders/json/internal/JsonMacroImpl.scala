package eu.inn.binders.json.internal

import eu.inn.binders.json.{JsonDeserializer, JsonSerializer}
import eu.inn.binders.naming.Converter

import scala.language.experimental.macros
import scala.language.reflectiveCalls
import scala.reflect.macros.Context

trait JsonMacroImpl {
  val c: Context

  import c.universe._

  def parseJson[C : c.WeakTypeTag, O: c.WeakTypeTag]: c.Tree = {
    val thisTerm = newTermName(c.fresh("$this"))
    val strTerm = newTermName("$str")
    val jsonParserTerm = newTermName(c.fresh("$jp"))
    val jsonDeserializerTerm = newTermName(c.fresh("$jds"))

    val vals = List(
      ValDef(Modifiers(), thisTerm, TypeTree(), c.prefix.tree),
      ValDef(Modifiers(), strTerm, TypeTree(), Select(Ident(thisTerm), newTermName("jsonString")))
    )

    val deserializeBlock = Block(
      // val jds = JsonDeserializer.apply[C](jp)
      ValDef(Modifiers(), jsonDeserializerTerm, TypeTree(),
        Apply(
          TypeApply(
            Select(Ident(typeOf[JsonDeserializer.type].termSymbol), newTermName("apply")),
            List(Ident(weakTypeOf[C].typeSymbol))
          ),
          List(Ident(jsonParserTerm))
        )
      ),
      // jds.unbind[O]
      TypeApply(
        Select(Ident(jsonDeserializerTerm), newTermName("unbind")),
        List(Ident(weakTypeOf[O].typeSymbol))
      )
    )

    val parseCodeFunc = Function(
      List(ValDef(Modifiers(Flag.PARAM), jsonParserTerm, TypeTree(), EmptyTree)),
      deserializeBlock
    )

    val block = Block(vals,
      Apply(
        TypeApply(Select(Ident(typeOf[JsonMacro.type].termSymbol), newTermName("wrapParser")),List(Ident(weakTypeOf[O].typeSymbol))),
        List(Ident(strTerm), parseCodeFunc)
      )
    )

    //println(s"parseJson(${weakTypeTag[C]}, ${weakTypeTag[O]}:\n$block)")
    block
  }

  def toJson[C : c.WeakTypeTag, O: c.WeakTypeTag]: c.Tree = {
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
        // val js = JsonSerializer.apply[C](jg)
        ValDef(Modifiers(), jsonSerializerTerm, TypeTree(),
          Apply(
            TypeApply(
              Select(Ident(typeOf[JsonSerializer.type].termSymbol), newTermName("apply")),
              List(Ident(weakTypeOf[C].typeSymbol))
            ),
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
    // println(block)
    block
  }

  protected def optionGetter[O: c.WeakTypeTag](value: c.Tree): c.Tree = {
    if (weakTypeTag[O].tpe <:< typeOf[Option[_]]) {
      Select(value, newTermName("get"))
    }
    else
      value
  }

  protected def applySome[T <: Tree](t: T) = Apply(Select(Ident(weakTypeOf[Some.type].termSymbol), "apply"),
    List(t))

  def setProduct[O: c.WeakTypeTag](name: c.Tree, value: c.Tree): c.Tree = {
    val nonNullBlock = Block(
      List(
        Apply(Select(c.prefix.tree, newTermName("beginObject")),List(applySome(name))),
        Apply(Select(c.prefix.tree, newTermName("bind")), List(optionGetter[O](value)))
      ),
      Apply(Select(c.prefix.tree, newTermName("endObject")),List())
    )

    val block = if (weakTypeTag[O].tpe <:< typeOf[Option[_]]) {
      If(
        Select(value, newTermName("isDefined")),
        nonNullBlock,
        Apply(Select(c.prefix.tree, newTermName("setNull")),List(name))
      )
    }
    else
      nonNullBlock
    // println(block)
    block
  }

  def addProduct[O: c.WeakTypeTag](value: c.Tree): c.Tree = {
    val nonNullBlock = Block(
      List(
        Apply(Select(c.prefix.tree, newTermName("beginObject")),List()),
        Apply(Select(c.prefix.tree, newTermName("bind")), List(optionGetter[O](value)))
      ),
      Apply(Select(c.prefix.tree, newTermName("endObject")),List())
    )

    val block = if (weakTypeTag[O].tpe <:< typeOf[Option[_]]) {
      If(
        Select(value, newTermName("isDefined")),
        nonNullBlock,
        Apply(Select(c.prefix.tree, newTermName("addNull")),List())
      )
    }
    else
      nonNullBlock
    // println(block)
    block
  }

  def setSequence[O: c.WeakTypeTag](name: c.Tree, value: c.Tree): c.Tree = {
    val elemTerm = newTermName(c.fresh("$elem"))
    val block = Block(
      List(
        Apply(Select(c.prefix.tree, newTermName("beginArray")),List(applySome(name))),
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
