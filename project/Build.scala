
import sbt._
import sbt.Default._
import sbt.Keys._

import scalariform.formatter.preferences._
import com.typesafe.sbtscalariform.ScalariformPlugin._

object AWS {
    val scalaVersion = "2.10.0-RC1"
    val version = "0.1-SNAPSHOT"
    val playVersion = "2.1-20121029-UP"
}

object ApplicationBuild extends Build {

    lazy val projectScalariformSettings = defaultScalariformSettings ++ Seq(
        ScalariformKeys.preferences := FormattingPreferences()
            .setPreference(AlignParameters, true)
            .setPreference(FormatXml, false)
    )

    lazy val commonSettings: Seq[Setting[_]] = Project.defaultSettings ++ projectScalariformSettings ++ Seq(
        organization := "aws",
        scalaVersion := AWS.scalaVersion,
        scalacOptions += "-feature",
        version := AWS.version,
        resolvers ++= Seq("typesafe" at "http://repo.typesafe.com/typesafe/releases",
          "Caffeine Lab" at "http://caffeinelab.net/repo"),
        resolvers ++= Seq("sonatype" at "http://oss.sonatype.org/content/repositories/releases"),
        libraryDependencies ++= Seq("play" %% "play" % AWS.playVersion,
          "org.specs2" % "specs2_2.10.0-RC1" % "1.12.2" % "test",
          "com.novocode" % "junit-interface" % "0.10-M2" % "test"),
        testOptions += Tests.Argument(TestFrameworks.JUnit, "-v")
        //testListeners <<= (target, streams).map((t, s) => Seq(new eu.henkelmann.sbt.JUnitXmlTestsListener(t.getAbsolutePath, s.log)))
    )

    lazy val core = Project("core", file("core"), settings = commonSettings ++ Seq(
        compileOrder := CompileOrder.Mixed
    ))

    lazy val s3 = Project("s3", file("s3"), settings = commonSettings).dependsOn(core)

    lazy val sqs = Project("sqs", file("sqs"), settings = commonSettings).dependsOn(core)

    lazy val sns = Project("sns", file("sns"), settings = commonSettings).dependsOn(core)

    lazy val dynamodb = Project("dynamodb", file("dynamodb"), settings = commonSettings ++ Seq(
        compileOrder := CompileOrder.ScalaThenJava
    )).dependsOn(core)

    lazy val simpledb = Project("simpledb", file("simpledb"), settings = commonSettings ++ Seq(
        compileOrder := CompileOrder.ScalaThenJava
    )).dependsOn(core)

    lazy val ses = Project("ses", file("ses"), settings = commonSettings).dependsOn(core)

    lazy val cloudsearxh = Project("cloud-search", file("cloudsearch"), settings = commonSettings).dependsOn(core)

    lazy val root = Project("root", file("."), settings = Project.defaultSettings ++ Unidoc.settings).aggregate(
        core, s3, simpledb, sqs, sns, dynamodb, ses
    )

}

