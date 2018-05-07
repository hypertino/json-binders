package com.hypertino.binders.json

import com.hypertino.binders.core.ImplicitSerializer

class Instant13TypeSerializer extends ImplicitSerializer[org.threeten.bp.Instant, JsonSerializer[_]] {
  override def write(serializer: JsonSerializer[_], value: org.threeten.bp.Instant): Unit = serializer.writeLong(value.toEpochMilli)
}

class InstantTypeSerializer extends ImplicitSerializer[java.time.Instant, JsonSerializer[_]] {
  override def write(serializer: JsonSerializer[_], value: java.time.Instant): Unit = serializer.writeLong(value.toEpochMilli)
}
