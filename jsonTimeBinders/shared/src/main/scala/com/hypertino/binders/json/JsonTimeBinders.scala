package com.hypertino.binders.json

object JsonTimeBinders {
  implicit val instant13Serializer = new Instant13TypeSerializer
  implicit val instant13Deserializer = new Instant13TypeDeserializer
  implicit val instantSerializer = new InstantTypeSerializer
  implicit val instantDeserializer = new InstantTypeDeserializer
}
