package com.hypertino.binders.json

import com.hypertino.binders.core.ImplicitSerializer
import org.threeten.bp.Instant

class InstantTypeSerializer extends ImplicitSerializer[Instant, JsonSerializer[_]] {
  override def write(serializer: JsonSerializer[_], value: Instant): Unit = serializer.writeLong(value.toEpochMilli)
}
