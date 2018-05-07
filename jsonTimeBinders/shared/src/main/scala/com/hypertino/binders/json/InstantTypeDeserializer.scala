package com.hypertino.binders.json

import com.hypertino.binders.core.ImplicitDeserializer

class Instant13TypeDeserializer extends ImplicitDeserializer[org.threeten.bp.Instant, JsonDeserializer[_]] {
  override def read(deserializer: JsonDeserializer[_]): org.threeten.bp.Instant = org.threeten.bp.Instant.ofEpochMilli(deserializer.readLong())
}

class InstantTypeDeserializer extends ImplicitDeserializer[java.time.Instant, JsonDeserializer[_]] {
  override def read(deserializer: JsonDeserializer[_]): java.time.Instant = java.time.Instant.ofEpochMilli(deserializer.readLong())
}
