package com.hypertino.binders.json

import com.hypertino.binders.core.{BindOptions, Deserializer}
import com.hypertino.binders.json.api._
import com.hypertino.binders.value.{Bool, Lst, Null, Number, Obj, Text, Value}
import com.hypertino.inflector.naming.Converter

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.duration._
import scala.language.experimental.macros

class JsonDeserializeException(message: String) extends RuntimeException(message)

abstract class JsonDeserializerBase[C <: Converter, I <: Deserializer[C]] (jsonParser: JsonParserApi, val moveToNextToken: Boolean, val fieldName: Option[String])
  extends Deserializer[C] {

  protected def bindOptions: BindOptions

  protected val currentToken: JsToken = if (moveToNextToken) nextToken() else jsonParser.currentToken

  def iterator(): Iterator[I] = {
    if (currentToken == JsStartArray) {
      createArrayIterator
    }
    else if (currentToken == JsStartObject) {
      createObjectIterator
    }
    else
      throw new JsonDeserializeException("Couldn't iterate non-array/non-object field. Current token: " + currentToken)
  }

  def consume(): Unit = {
    readValue() // todo: not very optimal, implement skipping
  }

  protected def createArrayIterator: Iterator[I] = new PrefetchIterator(JsEndArray, false)

  protected def createObjectIterator: Iterator[I] = new PrefetchIterator(JsEndObject, true)

  protected class PrefetchIterator(endToken: JsToken, moveToNextTokenForChildren: Boolean) extends Iterator[I] {
    var _hasNext = true
    var _moveNext = true
    nextIfNeeded()

    override def hasNext: Boolean = {
      nextIfNeeded()
      _hasNext
    }

    override def next(): I = {
      nextIfNeeded()
      _moveNext = true
      createFieldDeserializer(jsonParser, moveToNextTokenForChildren, jsonParser.fieldName)
    }

    def nextIfNeeded(): Unit = {
      if (_moveNext && _hasNext) {
        nextToken()
        _moveNext = false
        _hasNext = jsonParser.currentToken != endToken
      }
    }
  }

  protected def createFieldDeserializer(jsonParser: JsonParserApi, moveToNextToken: Boolean, fieldName: Option[String]): I

  protected def nextToken() : JsToken = {
    val token = jsonParser.nextToken()
    if (token == null)
      throw new JsonDeserializeException("Unexpected token: " + token + " offset: " + jsonParser.location)
    token
  }

  def isNull: Boolean = jsonParser.currentToken == JsNull

  private def optional[T](f : ⇒ T): Option[T] = {
    if (currentToken == JsNull)
      None
    else
      Some(f)
  }

  def readStringOption(): Option[String] = optional(readString())
  def readString(): String = jsonParser.stringValue
  def readIntOption(): Option[Int] = optional(readInt())
  def readInt(): Int = jsonParser.numberValue.toInt
  def readLongOption(): Option[Long] = optional(readLong())
  def readLong(): Long = jsonParser.numberValue.toLong
  def readDoubleOption(): Option[Double] = optional(readDouble())
  def readDouble(): Double = jsonParser.numberValue.toDouble
  def readFloatOption(): Option[Float] = optional(readFloat())
  def readFloat(): Float = jsonParser.numberValue.toFloat
  def readBooleanOption(): Option[Boolean] = optional(readBoolean())
  def readBoolean(): Boolean = currentToken match {
    case JsTrue ⇒ true
    case JsFalse ⇒ false
    case other ⇒ throw new JsonDeserializeException(s"Can't be read Boolean from '$other'")
  }
  def readBigDecimalOption(): Option[BigDecimal] = optional(readBigDecimal())
  def readBigDecimal(): BigDecimal = jsonParser.numberValue
  def readFiniteDuration(): FiniteDuration = jsonParser.numberValue.toLong.milliseconds
  def readFiniteDurationOption(): Option[FiniteDuration] = optional(readFiniteDuration())
  def readDuration(): Duration = Duration(jsonParser.stringValue)
  def readDurationOption: Option[Duration] = optional(readDuration())

  def readValue(): Value = {
    jsonParser.currentToken match {
      case JsNull => Null
      case JsTrue => Bool(true)
      case JsFalse => Bool(false)
      case JsString => Text(jsonParser.stringValue)
      case JsNumber => Number(jsonParser.numberValue)
      case JsStartObject =>
        var map = new scala.collection.mutable.HashMap[String, Value]()
        iterator().foreach(i => {
          val d = i.asInstanceOf[JsonDeserializerBase[_,_]]
          map += d.fieldName.get -> d.readValue()
        })
        Obj(map.toMap)

      case JsStartArray =>
        val array = new ArrayBuffer[Value]()
        iterator().foreach(i => array += i.asInstanceOf[JsonDeserializerBase[_,_]].readValue())
        Lst(array)

      case _ => throw new JsonDeserializeException(s"Can't deserialize token: ${jsonParser.currentToken} at ${jsonParser.location}")
    }
  }
}

class JsonDeserializer[C <: Converter] (jsonParser: JsonParserApi, override val moveToNextToken: Boolean = true, override val fieldName: Option[String] = None)
                                       (implicit protected val bindOptions: BindOptions)
  extends JsonDeserializerBase[C, JsonDeserializer[C]](jsonParser, moveToNextToken, fieldName) {
  protected override def createFieldDeserializer(jsonParser: JsonParserApi, moveToNextToken: Boolean, fieldName: Option[String]): JsonDeserializer[C] = new JsonDeserializer[C](jsonParser, moveToNextToken, fieldName)
}

object JsonDeserializer {

  private val precision = new java.math.MathContext(150)

  def stringToBigDecimal(s: String): BigDecimal ={
    BigDecimal(s, precision)
  }
}
