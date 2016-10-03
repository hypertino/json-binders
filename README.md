[![Build Status](https://travis-ci.org/hypertino/json-binders.svg)](https://travis-ci.org/hypertino/json-binders)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.hypertino/json-binders_2.11/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.hypertino/json-binders_2.11)

[ Latest releases and snapshots](https://oss.sonatype.org/#nexus-search;gav~com.hypertino~json-binders_*~~~)

# json-binders

[![Join the chat at https://gitter.im/Hypertino/json-binders](https://badges.gitter.im/Hypertino/json-binders.svg)](https://gitter.im/Hypertino/json-binders?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

Serialize/deserialize Scala case classes to JSON

A plain example on how-to use a library:
```scala
case class TestClass(
  intValue: Int,
  longOption: Option[Long],
  stringValue: String
)

//

import eu.inn.binders.json._

val obj = TestClass(100500,None,"John")

val json = obj.toJson // serialize to JSON string

val obj2 = json.parseJson[TestClass] // deserialize JSON string to case class
```


## License

Product licensed under BSD 3-clause as stated in file LICENSE
