import Dependencies._

ThisBuild / scalaVersion := "2.13.16"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "com.example"
ThisBuild / organizationName := "example"

lazy val commonSettings = Seq(
  scalacOptions ++= Seq(
    "-deprecation",
    "-feature",
    "-unchecked",
    "-Xlint:unused"
  )
)

lazy val domain = (project in file("psp-domain"))
  .settings(commonSettings)
  .settings(
    name := "psp-domain",
    libraryDependencies ++= Dependencies.domain
  )

lazy val core = (project in file("psp-core"))
  .settings(commonSettings)
  .settings(
    name := "psp-core",
    libraryDependencies ++= Dependencies.core
  )
  .dependsOn(domain)

lazy val storage = (project in file("psp-storage"))
  .settings(commonSettings)
  .settings(
    name := "psp-storage",
    libraryDependencies ++= Dependencies.storage
  )
  .dependsOn(domain, core)

lazy val acquirerMock = (project in file("psp-acquirer-mock"))
  .settings(commonSettings)
  .settings(
    name := "psp-acquirer-mock",
    libraryDependencies ++= Dependencies.acquirer
  )
  .dependsOn(domain, core)

lazy val api = (project in file("psp-api"))
  .settings(commonSettings)
  .settings(
    name := "psp-api",
    libraryDependencies ++= Dependencies.api
  )
  .dependsOn(domain, core, storage, acquirerMock)

lazy val app = (project in file("psp-app"))
  .settings(commonSettings)
  .settings(
    name := "psp-app",
    libraryDependencies ++= Dependencies.app
  )
  .dependsOn(api, core, storage, acquirerMock)

lazy val root = (project in file("."))
  .aggregate(domain, core, storage, acquirerMock, api, app)
  .settings(
    name := "PSP_System",
    publish / skip := true
  )
