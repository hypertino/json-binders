package com.hypertino.binders.json

import com.fasterxml.jackson.core.JsonToken
import com.hypertino.binders.core.BindOptions
import com.hypertino.binders.json.api._

class JacksonParserAdapter(jacksonParser: com.fasterxml.jackson.core.JsonParser)
                          (implicit protected val bindOptions: BindOptions) extends JsonParserApi {
  override def currentToken: JsToken = convertToken(jacksonParser.getCurrentToken)
  override def nextToken(): JsToken = convertToken(jacksonParser.nextToken())

  override def fieldName: Option[String] = Option(jacksonParser.getCurrentName)

  override def stringValue: String = jacksonParser.getValueAsString

  override def numberValue: BigDecimal = jacksonParser.getDecimalValue

  private def convertToken(token: JsonToken): JsToken = {
    token match {
      case JsonToken.START_OBJECT ⇒ JsStartObject
      case JsonToken.END_OBJECT ⇒ JsEndObject
      case JsonToken.START_ARRAY ⇒ JsStartArray
      case JsonToken.END_ARRAY ⇒ JsEndArray
      case JsonToken.FIELD_NAME ⇒ JsFieldName
      case JsonToken.VALUE_STRING ⇒ JsString
      case JsonToken.VALUE_NUMBER_FLOAT ⇒ JsNumber
      case JsonToken.VALUE_NUMBER_INT ⇒ JsNumber
      case JsonToken.VALUE_TRUE ⇒ JsTrue
      case JsonToken.VALUE_FALSE ⇒ JsFalse
      case JsonToken.VALUE_NULL ⇒ JsNull
      case _ ⇒ JsUnknown
    }
  }

  override def location: String = jacksonParser.getCurrentLocation.toString
}
