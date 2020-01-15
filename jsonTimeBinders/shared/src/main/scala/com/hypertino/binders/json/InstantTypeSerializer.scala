package com.hypertino.binders.json

import com.hypertino.binders.core.ImplicitSerializer

class InstantTypeSerializer extends ImplicitSerializer[java.time.Instant, JsonSerializer[_]] {
  override def write(serializer: JsonSerializer[_], value: java.time.Instant): Unit = serializer.writeLong(value.toEpochMilli)
}
