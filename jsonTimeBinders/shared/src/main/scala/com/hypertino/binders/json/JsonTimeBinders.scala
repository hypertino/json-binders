package com.hypertino.binders.json

object JsonTimeBinders {
  implicit val instantSerializer = new InstantTypeSerializer
  implicit val instantDeserializer = new InstantTypeDeserializer
}
