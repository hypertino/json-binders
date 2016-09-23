package com.hypertino.binders.json

import com.hypertino.binders.json.api.JsonGeneratorApi

import scala.collection.mutable
import scala.scalajs.js.JSON

// todo: implement pretty print
class JsGeneratorAdapter(val stringBuilder: StringBuilder) extends JsonGeneratorApi {
  var isSequence = false
  var prependComma = false
  val sequenceStack = mutable.Stack[Boolean]()

  override def writeNull(): Unit = {
    prependCommaIfNeeded()
    stringBuilder.append("null")
  }
  override def writeInt(value: Int): Unit = {
    prependCommaIfNeeded()
    stringBuilder.append(value.toString)
  }
  override def writeLong(value: Long): Unit = {
    prependCommaIfNeeded()
    stringBuilder.append(value.toString)
  }

  override def writeString(value: String): Unit = {
    prependCommaIfNeeded()
    stringBuilder.append(JSON.stringify(value))
  }

  override def writeFloat(value: Float): Unit = {
    prependCommaIfNeeded()
    stringBuilder.append(f"$value%g")
  }

  override def writeDouble(value: Double): Unit = {
    prependCommaIfNeeded()
    stringBuilder.append(value.toString)
  }

  override def writeBoolean(value: Boolean): Unit = {
    prependCommaIfNeeded()
    stringBuilder.append(if (value) "true" else "false")
  }

  override def writeBigDecimal(value: BigDecimal): Unit = {
    prependCommaIfNeeded()
    stringBuilder.append(value.toString)
  }

  override def writeStartObject(): Unit = {
    prependCommaIfNeeded()
    stringBuilder.append("{")
    sequenceStack.push(isSequence)
    isSequence = true
    prependComma = false
  }

  override def writeEndObject(): Unit = {
    stringBuilder.append("}")
    isSequence = sequenceStack.pop()
  }

  override def writeStartArray(): Unit = {
    prependCommaIfNeeded()
    stringBuilder.append("[")
    sequenceStack.push(isSequence)
    isSequence = true
    prependComma = false
  }

  override def writeEndArray(): Unit = {
    stringBuilder.append("]")
    isSequence = sequenceStack.pop()
  }

  override def writeFieldName(name: String): Unit = {
    prependCommaIfNeeded()
    stringBuilder.append(JSON.stringify(name))
    stringBuilder.append(":")
    prependComma = false
  }

  private def prependCommaIfNeeded(): Unit = {
    if (prependComma) {
      stringBuilder.append(',')
    }
    if (isSequence) {
      prependComma = true
    }
  }
}
