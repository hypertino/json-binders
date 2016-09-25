[![Build Status](https://travis-ci.org/hypertino/binders-json.svg)](https://travis-ci.org/hypertino/binders-json)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.hypertino/binders-json_2.11/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.hypertino/binders-json_2.11)
[ Latest Releases ](https://oss.sonatype.org/#nexus-search;gav~com.hypertino~binders-json_*~~~)

# binders-json

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
