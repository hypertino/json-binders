import sbt.Keys._

version in Global := "1.0-SNAPSHOT"

crossScalaVersions := Seq("2.12.1", "2.11.8", "2.10.6")

scalaVersion in Global := "2.12.1"

organization in Global := "com.hypertino"

lazy val jsonBinders = crossProject.settings(publishSettings:_*).settings(
    name := "json-binders",
    libraryDependencies ++= Seq(
      "com.hypertino" %%% "binders" % "1.0-SNAPSHOT",
      "org.scalamock" %%% "scalamock-scalatest-support" % "3.6.0" % "test",
      "org.scala-lang" % "scala-reflect" % scalaVersion.value
    ) ++ {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, 10)) =>
          Seq(compilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full),
            "org.scalamacros" %% "quasiquotes" % "2.1.0" cross CrossVersion.binary)
        case _ ⇒ Seq.empty
      }
    },
    publishArtifact := true,
    publishArtifact in Test := false,
    resolvers ++= Seq(
      Resolver.sonatypeRepo("public")
    )
  )
  .jsSettings(
  )
  .jvmSettings(
    libraryDependencies ++= Seq(
      "com.fasterxml.jackson.core" % "jackson-core" % "2.9.0"
    )
  )

lazy val jsonBindersJS = jsonBinders.js

lazy val jsonBindersJVM = jsonBinders.jvm

lazy val jsonTimeBinders = crossProject.dependsOn(jsonBinders).settings(publishSettings:_*).settings(
  name := "json-time-binders",
  libraryDependencies ++= Seq(
    "io.github.soc" %%% "scala-java-time" % "2.0.0-M12",
    "com.hypertino" %%% "scalamock-scalatest-support" % "3.6.0" % "test",
    "org.scala-lang" % "scala-reflect" % scalaVersion.value
  ) ++ {
    CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, 10)) =>
        Seq(compilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full),
          "org.scalamacros" %% "quasiquotes" % "2.1.0" cross CrossVersion.binary)
      case _ ⇒ Seq.empty
    }
  },
  publishArtifact := true,
  publishArtifact in Test := false,
  resolvers ++= Seq(
    Resolver.sonatypeRepo("public")
  )
)
  .jsSettings(
  )
  .jvmSettings(
  )

lazy val jsonTimeBindersJS = jsonTimeBinders.js

lazy val jsonTimeBindersJVM = jsonTimeBinders.jvm

lazy val benchTest = crossProject.dependsOn(jsonBinders).enablePlugins(JmhPlugin).settings(
  name := "bench-test",
  libraryDependencies ++= Seq(
    "com.lihaoyi" %%% "upickle" % "0.4.4",
    "org.scala-lang" % "scala-reflect" % scalaVersion.value
  ) ++ {
    CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, 10)) =>
        Seq(compilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full),
          "org.scalamacros" %% "quasiquotes" % "2.1.0" cross CrossVersion.binary)
      case _ ⇒ Seq.empty
    }
  },
  publishArtifact := false,
  publishArtifact in Test := false,
  publish := (),
  publishLocal := (),
  resolvers ++= Seq(
    Resolver.sonatypeRepo("public")
  )
)
  .jsSettings(
    scalaJSStage in Global := FullOptStage,
    scalaJSUseRhino := false,
    scalaJSUseRhino in Test := true
  )
  .jvmSettings(
  )

//lazy val benchTestJS = benchTest.js
//
//lazy val benchTestJVM = benchTest.jvm


val publishSettings = Seq(
  pomExtra := <url>https://github.com/hypertino/json-binders</url>
    <licenses>
      <license>
        <name>BSD-style</name>
        <url>http://opensource.org/licenses/BSD-3-Clause</url>
        <distribution>repo</distribution>
      </license>
    </licenses>
    <scm>
      <url>git@github.com:hypertino/json-binders.git</url>
      <connection>scm:git:git@github.com:hypertino/json-binders.git</connection>
    </scm>
    <developers>
      <developer>
        <id>maqdev</id>
        <name>Magomed Abdurakhmanov</name>
        <url>https://github.com/maqdev</url>
      </developer>
      <developer>
        <id>hypertino</id>
        <name>Hypertino</name>
        <url>https://github.com/hypertino</url>
      </developer>
    </developers>,
  pgpSecretRing := file("./travis/ht-oss-private.asc"),
  pgpPublicRing := file("./travis/ht-oss-public.asc"),
  usePgpKeyHex("F8CDEF49B0EDEDCC"),
  pgpPassphrase := Option(System.getenv().get("oss_gpg_passphrase")).map(_.toCharArray),
  publishMavenStyle := true,
  pomIncludeRepository := { _ => false},
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value)
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases" at nexus + "service/local/staging/deploy/maven2")
  }
)

credentials in Global ++= (for {
  username <- Option(System.getenv().get("sonatype_username"))
  password <- Option(System.getenv().get("sonatype_password"))
} yield Credentials("Sonatype Nexus Repository Manager", "oss.sonatype.org", username, password)).toSeq


publishArtifact in Test := false

publishArtifact in Global := false

publish in Global := ()

publishLocal in Global := ()

scalacOptions in Global ++= Seq("-feature", "-deprecation")
