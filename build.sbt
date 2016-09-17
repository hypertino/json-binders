name := "binders-json"

crossScalaVersions := Seq("2.11.8"/*, "2.10.6"*/)

organization in Global := "com.hypertino"

lazy val bindersJson = crossProject.settings(publishSettings:_*).settings(
    name := "binders-json",
    version := "1.0-SNAPSHOT",
    scalaVersion := "2.11.8",
    libraryDependencies ++= Seq(
      "com.hypertino" %%% "binders" % "1.0-SNAPSHOT",
      "org.scalatest" %%% "scalatest" % "3.0.0" % "test",
      "com.hypertino" %%% "scalamock-scalatest-support" % "3.4-SNAPSHOT" % "test",
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
      "com.fasterxml.jackson.core" % "jackson-core" % "2.7.3",
      "org.mockito" % "mockito-all" % "1.10.19" % "test"
    )
  )

lazy val js = bindersJson.js

lazy val jvm = bindersJson.jvm

val publishSettings = Seq(
  pomExtra := <url>https://github.com/hypertino/binders-json</url>
    <licenses>
      <license>
        <name>BSD-style</name>
        <url>http://opensource.org/licenses/BSD-3-Clause</url>
        <distribution>repo</distribution>
      </license>
    </licenses>
    <scm>
      <url>git@github.com:hypertino/binders-json.git</url>
      <connection>scm:git:git@github.com:hypertino/binders-json.git</connection>
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


credentials ++= (for {
  username <- Option(System.getenv().get("sonatype_username"))
  password <- Option(System.getenv().get("sonatype_password"))
} yield Credentials("Sonatype Nexus Repository Manager", "oss.sonatype.org", username, password)).toSeq


publishArtifact in Test := false

publishArtifact := false

publish := ()

publishLocal := ()
