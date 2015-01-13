
import eu.inn.binders.naming.CamelCaseToSnakeCaseConverter
import org.scalatest.{FlatSpec, Matchers}


class TestConvertJsonSerializer extends FlatSpec with Matchers {

   import eu.inn.binders.json._

   "Json " should " serialize class with Int with Converter" in {
     implicit val factory = new DefaultSerializerFactory[CamelCaseToSnakeCaseConverter]

     val t = TestInt(1234)
     val str = t.toJson
     assert (str === """{"int_val":1234}""")
   }
}