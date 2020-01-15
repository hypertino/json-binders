package com.hypertino.binders.json

import com.hypertino.binders.core.ImplicitDeserializer

class InstantTypeDeserializer extends ImplicitDeserializer[java.time.Instant, JsonDeserializer[_]] {
  override def read(deserializer: JsonDeserializer[_]): java.time.Instant = java.time.Instant.ofEpochMilli(deserializer.readLong())
}
