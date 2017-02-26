package com.hypertino.binders.json.api

import com.hypertino.binders.core.BindOptions

trait JsonParserApi {
  def currentToken: JsToken
  def nextToken(): JsToken
  def fieldName: Option[String]
  def stringValue: String
  def numberValue: BigDecimal
  def location: String
  protected def bindOptions: BindOptions
}

sealed trait JsToken
case object JsUnknown extends JsToken
case object JsStartObject extends JsToken
case object JsEndObject extends JsToken
case object JsStartArray extends JsToken
case object JsEndArray extends JsToken
case object JsFieldName extends JsToken
case object JsString extends JsToken
case object JsNumber extends JsToken
case object JsTrue extends JsToken
case object JsFalse extends JsToken
case object JsNull extends JsToken
