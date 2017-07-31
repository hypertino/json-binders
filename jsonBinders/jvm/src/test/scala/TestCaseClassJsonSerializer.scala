
import com.hypertino.binders.json.{DefaultJsonBindersFactory, JsonBinders}
import com.hypertino.inflector.naming.CamelCaseToSnakeCaseConverter
import org.scalatest.{FlatSpec, Matchers}

case class TestCaseClass3(userId: String)

class TestCaseClassJsonSerializerX extends FlatSpec with Matchers {
  import JsonBinders._

  "Json " should " deserialize class with extra fields" in {
    val s =
      """
        {
          "test":{"x":"y"},
          "userId":"101396227229647"
        }
      """

    s.parseJson[TestCaseClass3]
  }
}
