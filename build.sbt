name := "binders-json"

version := "0.6"

organization := "eu.inn"

scalaVersion := "2.11.7"

crossScalaVersions := Seq("2.11.7", "2.10.5")

resolvers ++= Seq(
    Resolver.sonatypeRepo("public")
  )

libraryDependencies += "org.mockito" % "mockito-all" % "1.10.19" % "test"

libraryDependencies += "com.fasterxml.jackson.core" % "jackson-core" % "2.4.4"

libraryDependencies += "eu.inn" %% "binders-core" % "0.10.70"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.1" % "test"

libraryDependencies := {
  CrossVersion.partialVersion(scalaVersion.value) match {
    // if scala 2.11+ is used, quasiquotes are merged into scala-reflect
    case Some((2, scalaMajor)) if scalaMajor >= 11 =>
      libraryDependencies.value
    // in Scala 2.10, quasiquotes are provided by macro paradise
    case Some((2, 10)) =>
      libraryDependencies.value ++ Seq(
        compilerPlugin("org.scalamacros" % "paradise" % "2.1.0-M5" cross CrossVersion.full),
        "org.scalamacros" %% "quasiquotes" % "2.1.0-M5" cross CrossVersion.binary)
  }
}
