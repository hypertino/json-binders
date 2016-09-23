package com.hypertino.binders.json.api

import com.hypertino.binders.core.{Deserializer, Serializer}
import com.hypertino.inflector.naming.Converter

trait SerializerFactoryApi[C <: Converter, S <: Serializer[C], D <: Deserializer[C]] {
  def withStringParser[T](jsonString: String)(codeBlock: D ⇒ T): T
  def withStringGenerator(codeBlock: S ⇒ Unit): String
}
