package com.hypertino.binders.json

import com.hypertino.binders.core.ImplicitDeserializer
import org.threeten.bp.Instant

class InstantTypeDeserializer extends ImplicitDeserializer[Instant, JsonDeserializer[_]] {
  override def read(deserializer: JsonDeserializer[_]): Instant = Instant.ofEpochMilli(deserializer.readLong())
}
