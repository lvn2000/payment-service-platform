import sbt._

object Dependencies {
  private val catsEffect = "org.typelevel" %% "cats-effect" % "3.5.4"
  private val catsCore = "org.typelevel" %% "cats-core" % "2.12.0"

  private val http4sV = "0.23.27"
  private val http4sDsl = "org.http4s" %% "http4s-dsl" % http4sV
  private val http4sServer = "org.http4s" %% "http4s-ember-server" % http4sV
  private val http4sCirce = "org.http4s" %% "http4s-circe" % http4sV

  private val tapirV = "1.10.8"
  private val tapirCore = "com.softwaremill.sttp.tapir" %% "tapir-core" % tapirV
  private val tapirJson =
    "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % tapirV
  private val tapirHttp4s =
    "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % tapirV
  private val tapirSwagger =
    "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % tapirV

  private val circeV = "0.14.10"
  private val circeCore = "io.circe" %% "circe-core" % circeV
  private val circeGeneric = "io.circe" %% "circe-generic" % circeV
  private val circeParser = "io.circe" %% "circe-parser" % circeV

  private val logback = "ch.qos.logback" % "logback-classic" % "1.5.6"
  private val log4cats = "org.typelevel" %% "log4cats-slf4j" % "2.7.0"

  private val munit = "org.scalameta" %% "munit" % "1.0.0" % Test

  val domain = Seq(catsCore, munit)
  val core = Seq(catsEffect, catsCore, munit)
  val storage = Seq(catsEffect, munit)
  val acquirer = Seq(catsEffect, munit)
  val api = Seq(
    http4sDsl,
    http4sServer,
    http4sCirce,
    tapirCore,
    tapirJson,
    tapirHttp4s,
    tapirSwagger,
    circeCore,
    circeGeneric,
    circeParser,
    munit
  )
  val app = Seq(catsEffect, logback, log4cats, munit)
  val it = Seq(munit)
}
